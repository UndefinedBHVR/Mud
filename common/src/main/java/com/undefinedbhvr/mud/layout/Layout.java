package com.undefinedbhvr.mud.layout;

import com.undefinedbhvr.mud.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * The Layout class is responsible for managing UI element hierarchies and their positioning.
 * It provides a fluent API for creating, configuring, and nesting UI elements.
 * Layouts are initialized with screen dimensions and facilitate the construction of 
 * complex UI structures through a builder-like pattern.
 */
public class Layout {
    private final Context context = new Context();
    // List of completed root elements
    private final List<Element> elements = new ArrayList<>();
    private final Element rootElement;
    private Element currentElement = null;

    /**
     * Gets the root element of this layout.
     * 
     * @return The root element of the layout hierarchy
     */
    public Element getRootElement() {
        return rootElement;
    }

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
     * Closes the current element and returns to the parent element.
     * This method adjusts the parent's dimensions based on the current element's size
     * and the layout direction. It also ensures the element's dimensions are clamped
     * to its min/max constraints.
     * 
     * @throws IllegalStateException if there is no current element to close
     */
    public void closeElement() {
        if (currentElement == null) {
            throw new IllegalStateException("No current element to close");
        }

        if (currentElement.getParent() != null) {
            final Element parent = currentElement.getParent();
            final int childGap = (parent.getChildren().size() - 1) * parent.getChildren().size();
            // Adjust the parent's width and height
            if (parent.getDirection() == Direction.Horizontal) {
                currentElement.setWidth(currentElement.getWidth() + childGap);
                parent.setWidth(parent.getWidth() + currentElement.getWidth());
                parent.setMinWidth(parent.getMinWidth() + currentElement.getMinWidth());
                parent.setHeight(Math.max(parent.getHeight(), currentElement.getHeight()));
                parent.setMinHeight(Math.max(parent.getMinHeight(), currentElement.getMinHeight()));
            } else {
                currentElement.setHeight(currentElement.getHeight() + childGap);
                parent.setHeight(parent.getHeight() + currentElement.getHeight());
                parent.setMinHeight(parent.getMinHeight() + currentElement.getMinHeight());
                parent.setWidth(Math.max(parent.getWidth(), currentElement.getWidth()));
                parent.setMinWidth(Math.max(parent.getMinWidth(), currentElement.getMinWidth()));
            }
        }

        // Clamp us between our min and max sizes
        currentElement.setHeight(Math.min(Math.max(currentElement.getHeight(), currentElement.getMinHeight()), currentElement.getMaxHeight()));
        currentElement.setWidth(Math.min(Math.max(currentElement.getWidth(), currentElement.getMinWidth()), currentElement.getMaxWidth()));

        if (currentElement.getParent() != null) {
            // Set the current element back to its parent
            currentElement = currentElement.getParent();
        }
    }

    /**
     * Convenience method to create, configure, and close an element in one operation.
     * 
     * @param id The unique identifier for the new element
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
     * Finalizes the layout by ensuring all elements are closed, growing children
     * according to their sizing strategies, and positioning all elements.
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

        // Finalize the layout by growing children
        for (Element element : elements) {
            element.growChildren();
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
     * @param depth The current depth in the tree (for indentation)
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

