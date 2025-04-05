package com.undefinedbhvr.mud.layout;

import com.undefinedbhvr.mud.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * The Layout class is responsible for managing the layout of a UI hierarchy
 * within and arbitrarily sized screen.
 * Layouts are created by initializing the class then chaining calls to openElement()
 * which provides a lambda to configure the element.
 */
public class Layout {
    private final Context context = new Context();
    // List of completed root elements
    private final List<Element> elements = new ArrayList<>();
    // List of elements in depth first post-order traversal
    private final List<Element> dsaPostOrder = new ArrayList<>();
    private final Element rootElement;
    private Element currentElement = null;

    /**
     * Creates a new layout with specified screen dimensions.
     * Initializes a root element sized to the screen dimensions.
     *
     * @param screenX The width of the screen in pixels
     * @param screenY The height of the screen in pixels
     */
    public Layout(int screenX, int screenY) {
        createElement("root");
        rootElement = currentElement;
        rootElement.setxSizing(new Sizing.Fixed(screenX));
        rootElement.setySizing(new Sizing.Fixed(screenY));
    }

    /**
     * Gets the root element of this layout.
     *
     * @return The root element of the layout hierarchy
     */
    public Element getRootElement() {
        return rootElement;
    }

    /**
     * Creates a new element as the child of the current element, and makes it the current element.
     * If there is no current element, creates a root element.
     * The new element is assigned a random UUID.
     */
    public void createElement() {
        // Create a new element with the current element as parent
        Element newElement = new Element(currentElement);
        if (currentElement != null) {
            currentElement.addChild(newElement);
            newElement.setParent(currentElement);
        }
        elements.add(newElement);
        currentElement = newElement;
    }

    /**
     * Creates a new element with the specified ID as the child of the current element,
     * and makes it the current element.
     *
     * @param id The unique identifier for the new element
     */
    public void createElement(String id) {
        // Create a new element with the given ID
        Element newElement = new Element(currentElement, id);
        if (currentElement != null) {
            currentElement.addChild(newElement);
            newElement.setParent(currentElement);
        }
        elements.add(newElement);
        currentElement = newElement;
    }

    /**
     * Returns the current element for configuration.
     *
     * @return The current element being worked on
     */
    public Element configureElement() {
        return currentElement;
    }

    /**
     * Closes the current element and returns to its parent element.
     */
    public void closeElement() {
        dsaPostOrder.add(currentElement);

        if (currentElement.getParent() != null) {
            currentElement = currentElement.getParent();
        }
    }


    /**
     * Sizes the element's width based on its parent's direction and alignment.
     */
    private void sizeElementWidth(Element currentElement) {
        if (currentElement.getParent() != null) {
            final Element parent = currentElement.getParent();
            boolean isHorizontal = parent.getDirection() == Direction.Horizontal;

            if (isHorizontal) {
                sizeAlongFlowWidth(currentElement, parent);
            } else {
                sizeAcrossFlowWidth(currentElement, parent);
            }
        }

        clampElementWidth(currentElement);
    }

    /**
     * Sizes the element's height based on its parent's direction and alignment.
     */
    private void sizeElementHeight(Element currentElement) {
        if (currentElement.getParent() != null) {
            final Element parent = currentElement.getParent();
            boolean isHorizontal = parent.getDirection() == Direction.Horizontal;

            if (isHorizontal) {
                sizeAcrossFlowHeight(currentElement, parent);
            } else {
                sizeAlongFlowHeight(currentElement, parent);
            }
        }

        clampElementHeight(currentElement);
    }

    private void sizeAlongFlowWidth(Element currentElement, Element parent) {
        final int childGap = (parent.getChildren().size() - 1) * parent.getChildren().size();

        currentElement.setWidth(currentElement.getWidth() + childGap);
        parent.setWidth(parent.getWidth() + currentElement.getWidth());
        parent.setMinWidth(parent.getMinWidth() + currentElement.getMinWidth());
    }

    private void sizeAlongFlowHeight(Element currentElement, Element parent) {
        final int childGap = (parent.getChildren().size() - 1) * parent.getChildren().size();

        currentElement.setHeight(currentElement.getHeight() + childGap);
        parent.setHeight(parent.getHeight() + currentElement.getHeight());
        parent.setMinHeight(parent.getMinHeight() + currentElement.getMinHeight());
    }

    private void sizeAcrossFlowWidth(Element currentElement, Element parent) {
        parent.setWidth(Math.max(parent.getWidth(), currentElement.getWidth()));
        parent.setMinWidth(Math.max(parent.getMinWidth(), currentElement.getMinWidth()));
    }

    private void sizeAcrossFlowHeight(Element currentElement, Element parent) {
        parent.setHeight(Math.max(parent.getHeight(), currentElement.getHeight()));
        parent.setMinHeight(Math.max(parent.getMinHeight(), currentElement.getMinHeight()));
    }

    private void clampElementWidth(Element currentElement) {
        currentElement.setWidth(Math.min(Math.max(currentElement.getWidth(), currentElement.getMinWidth()), currentElement.getMaxWidth()));
    }

    private void clampElementHeight(Element currentElement) {
        currentElement.setHeight(Math.min(Math.max(currentElement.getHeight(), currentElement.getMinHeight()), currentElement.getMaxHeight()));
    }

    /**
     * Convenience method to create, configure, and close an element in one operation.
     *
     * @param id         The unique identifier for the new element
     * @param configurer A consumer function that configures the created element
     */
    public void openElement(String id, java.util.function.Consumer<Element> configurer) {
        // Create the element
        createElement(id);

        // Allow configuration
        Element element = configureElement();
        configurer.accept(element);

        // Automatically close the element
        closeElement();
    }

    /**
     * Finalizes the layout by ensuring all elements are closed and then does all the layout passes.
     *
     * @throws IllegalStateException if not all elements have been closed
     */
    public void finalizeLayout() {
        // Ensure we're back at the root element
        if (currentElement != rootElement) {
            throw new IllegalStateException("Not all elements have been closed");
        }

        // Close our root element
        closeElement();

        // Width sizing pass
        for (Element element : dsaPostOrder) {
            sizeElementWidth(element);
        }

        // Grow width pass
        for (Element element : elements) {
            element.growChildrenWidth();
        }

        // Text Wrapping Pass (unimplemented)

        // Height sizing pass
        for (Element element : dsaPostOrder) {
            sizeElementHeight(element);
        }

        // Then grow height dimensions
        for (Element element : elements) {
            element.growChildrenHeight();
        }

        // Position all elements
        rootElement.setScreenPositionX(0);
        rootElement.setScreenPositionY(0);
        positionElements(rootElement);
    }

    /**
     * Recursively positions all children of an element based on layout constraints.
     * Computes absolute screen positions for each element in the hierarchy.
     *
     * @param parent The parent element whose children need positioning
     */
    private void positionElements(Element parent) {
        if (parent.getChildren().isEmpty()) {
            return;
        }

        float currentX = parent.getScreenPositionX() + parent.getPaddingLeft();
        float currentY = parent.getScreenPositionY() + parent.getPaddingTop();

        float unusedWidth = parent.getWidth();
        float unusedHeight = parent.getHeight();
        for (Element child : parent.getChildren()) {
            // Walk through the children so we can use their sizes to determine alignment
            unusedWidth -= child.getWidth();
            unusedHeight -= child.getHeight();
        }

        for (Element child : parent.getChildren()) {
            // We can use the unused space to create an offset value for ourselves.
            float xOffset = 0;
            float yOffset = 0;
            switch (parent.getDirection()) {
                case Horizontal -> {
                    switch (parent.getxAlign()) {
                        case Start -> {
                            // Nothing to do here.
                        }
                        case Center -> {
                            xOffset += unusedWidth / 2;
                        }
                        case End -> {
                            xOffset -= unusedWidth;
                        }
                    }
                    float remainingY = parent.getHeight() - child.getHeight();
                    switch (parent.getyAlign()) {
                        case Start -> {
                            // Nothing to do here.
                        }
                        case Center -> {
                            yOffset += remainingY / 2;
                        }
                        case End -> {
                            yOffset += remainingY;
                        }
                    }
                }
                case Vertical -> {
                    float remainingX = parent.getWidth() - child.getWidth();
                    switch (parent.getxAlign()) {
                        case Start -> {
                            // Nothing to do here.
                        }
                        case Center -> {
                            xOffset += remainingX / 2;
                        }
                        case End -> {
                            xOffset += remainingX;
                        }
                    }
                    switch (parent.getyAlign()) {
                        case Start -> {
                            // Nothing to do here.
                        }
                        case Center -> {
                            yOffset += unusedHeight / 2;
                        }
                        case End -> {
                            yOffset -= unusedHeight;
                        }
                    }
                }
            }

            child.setScreenPositionX(currentX + xOffset);
            child.setScreenPositionY(currentY + yOffset);

            if (parent.getDirection() == Direction.Horizontal) {
                currentX += child.getWidth() + parent.getChildGap();
            } else {
                currentY += child.getHeight() + parent.getChildGap();
            }

            // Recursively position this child's children
            positionElements(child);
        }
    }

    /**
     * Pretty prints the element tree hierarchy starting from root elements.
     * Outputs information about each element's ID, dimensions, direction and position.
     */
    public void prettyPrintElementTree() {
        Constants.LOG.info("Element Tree:");

        // Find and print only true root elements (elements with no parent)
        for (Element element : elements) {
            if (element.getParent() == null) {
                printElementTree(element, 0);
            }
        }
    }

    /**
     * Recursively prints an element and its children with proper indentation.
     *
     * @param element The element to print
     * @param depth   The current depth in the tree (for indentation)
     */
    private void printElementTree(Element element, int depth) {
        // Create indentation based on depth
        String indent = "  ".repeat(depth);

        // Print element details
        Constants.LOG.info(indent + "- Element [ID: " + element.getId() + "]");
        Constants.LOG.info(indent + "  Width: " + element.getWidth() + ", Height: " + element.getHeight());
        Constants.LOG.info(indent + "  Direction: " + element.getDirection());
        Constants.LOG.info(indent + "  Position: (" + element.getScreenPositionX() + ", " + element.getScreenPositionY() + ")");

        // Print children
        if (!element.getChildren().isEmpty()) {
            Constants.LOG.info(indent + "  Children:");
            for (Element child : element.getChildren()) {
                printElementTree(child, depth + 1);
            }
        }
    }
}

