package com.undefinedbhvr.mud.layout;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Represents a UI layout element in the Mud UI system.
 * Elements can be arranged in a hierarchy, with parent-child relationships.
 * Each element manages its sizing, positioning, padding, and layout direction.
 * Elements can be configured to grow, shrink, or maintain fixed dimensions.
 */
public class Element {
    // The list of child elements
    private final List<Element> children = new ArrayList<>();
    private String id = UUID.randomUUID().toString();
    private Direction direction = Direction.Horizontal;
    // By default, we want to hug the size of the children
    private Sizing xSizing = new Sizing.Hug();
    private Sizing ySizing = new Sizing.Hug();
    // Padding is the space between the parent and its children.
    private float paddingTop;
    private float paddingRight;
    private float paddingBottom;
    private float paddingLeft;
    // How much space to leave between children
    private float childGap;
    // The parent element, or null if this is a root element
    private Element parent;
    private float width = 0;
    private float height = 0;
    private float minWidth = 0;
    private float minHeight = 0;
    private float maxWidth = Float.POSITIVE_INFINITY;
    private float maxHeight = Float.POSITIVE_INFINITY;
    // Screen positions (absolute coordinates)
    private float screenPositionX = 0;
    private float screenPositionY = 0;
    private Alignment xAlign = Alignment.Start;
    private Alignment yAlign = Alignment.Start;

    /**
     * Creates a root element with the default ID "root".
     */
    Element() {
        this.id = "root";
    }

    /**
     * Creates an element with a given parent.
     *
     * @param parent The parent element to which this element will be attached
     */
    Element(Element parent) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.parent = parent;
    }

    /**
     * Creates an element with a given parent and specific ID.
     *
     * @param parent The parent element to which this element will be attached
     * @param id     The unique identifier for this element
     */
    Element(Element parent, String id) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.parent = parent;
    }

    /**
     * Gets the unique identifier of this element.
     *
     * @return The element's ID
     */
    public String getId() {
        return id;
    }

    /**
     * Adds a child element to this element.
     *
     * @param child The element to add as a child
     * @return This element for method chaining
     */
    Element addChild(Element child) {
        children.add(child);
        return this;
    }

    /**
     * Gets the horizontal sizing strategy for this element.
     *
     * @return The current horizontal sizing strategy
     */
    public Sizing getxSizing() {
        return xSizing;
    }

    /**
     * Sets the horizontal sizing strategy for this element.
     * If using fixed sizing, the element's width, minWidth and maxWidth will be set to the specified value.
     *
     * @param xSizing The sizing strategy to use horizontally
     * @return This element for method chaining
     */
    public Element setxSizing(Sizing xSizing) {
        this.minWidth = xSizing.getMin();
        this.maxWidth = xSizing.getMax();
        if (xSizing instanceof Sizing.Fixed) {
            this.width = xSizing.getMin();
        }
        this.xSizing = xSizing;
        return this;
    }

    /**
     * Gets the vertical sizing strategy for this element.
     *
     * @return The current vertical sizing strategy
     */
    public Sizing getySizing() {
        return ySizing;
    }

    /**
     * Sets the vertical sizing strategy for this element.
     * If using fixed sizing, the element's height, minHeight and maxHeight will be set to the specified value.
     *
     * @param ySizing The sizing strategy to use vertically
     * @return This element for method chaining
     */
    public Element setySizing(Sizing ySizing) {
        this.minHeight = ySizing.getMin();
        this.maxHeight = ySizing.getMax();
        if (ySizing instanceof Sizing.Fixed) {
            this.height = ySizing.getMin();
        }
        this.ySizing = ySizing;
        return this;
    }

    /**
     * Gets the top padding of this element.
     *
     * @return The top padding in pixels
     */
    public float getPaddingTop() {
        return paddingTop;
    }

    /**
     * Sets the top padding of this element.
     *
     * @param paddingTop The top padding to set in pixels
     * @return This element for method chaining
     */
    public Element setPaddingTop(float paddingTop) {
        this.paddingTop = paddingTop;
        return this;
    }

    /**
     * Gets the right padding of this element.
     *
     * @return The right padding in pixels
     */
    public float getPaddingRight() {
        return paddingRight;
    }

    /**
     * Sets the right padding of this element.
     *
     * @param paddingRight The right padding to set in pixels
     * @return This element for method chaining
     */
    public Element setPaddingRight(float paddingRight) {
        this.paddingRight = paddingRight;
        return this;
    }

    /**
     * Gets the bottom padding of this element.
     *
     * @return The bottom padding in pixels
     */
    public float getPaddingBottom() {
        return paddingBottom;
    }

    /**
     * Sets the bottom padding of this element.
     *
     * @param paddingBottom The bottom padding to set in pixels
     * @return This element for method chaining
     */
    public Element setPaddingBottom(float paddingBottom) {
        this.paddingBottom = paddingBottom;
        return this;
    }

    /**
     * Gets the left padding of this element.
     *
     * @return The left padding in pixels
     */
    public float getPaddingLeft() {
        return paddingLeft;
    }

    /**
     * Sets the left padding of this element.
     *
     * @param paddingLeft The left padding to set in pixels
     * @return This element for method chaining
     */
    public Element setPaddingLeft(float paddingLeft) {
        this.paddingLeft = paddingLeft;
        return this;
    }

    /**
     * Gets the gap between child elements.
     *
     * @return The gap size in pixels
     */
    public float getChildGap() {
        return childGap;
    }

    /**
     * Sets the gap between child elements.
     *
     * @param childGap The gap size to set in pixels
     * @return This element for method chaining
     */
    public Element setChildGap(float childGap) {
        this.childGap = childGap;
        return this;
    }

    /**
     * Gets the parent element of this element.
     *
     * @return The parent element, or null if this is a root element
     */
    public Element getParent() {
        return parent;
    }

    /**
     * Sets the parent element of this element.
     *
     * @param parent The parent element to set
     * @return This element for method chaining
     */
    public Element setParent(Element parent) {
        this.parent = parent;
        return this;
    }

    /**
     * Gets the layout direction of this element.
     *
     * @return The layout direction (Horizontal or Vertical)
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Sets the layout direction of this element.
     *
     * @param direction The layout direction to set
     * @return This element for method chaining
     */
    public Element setDirection(Direction direction) {
        this.direction = direction;
        return this;
    }

    /**
     * Gets the height of this element.
     *
     * @return The height in pixels
     */
    public float getHeight() {
        return height;
    }

    /**
     * Sets the height of this element.
     *
     * @param height The height to set in pixels
     * @return This element for method chaining
     */
    public Element setHeight(float height) {
        this.height = height;
        return this;
    }

    /**
     * Gets the width of this element.
     *
     * @return The width in pixels
     */
    public float getWidth() {
        return width;
    }

    /**
     * Sets the width of this element.
     *
     * @param width The width to set in pixels
     * @return This element for method chaining
     */
    public Element setWidth(float width) {
        this.width = width;
        return this;
    }

    /**
     * Gets the list of child elements.
     *
     * @return The list of child elements
     */
    public List<Element> getChildren() {
        return children;
    }

    /**
     * Gets the absolute X position of this element on the screen.
     *
     * @return The X position in pixels
     */
    public float getScreenPositionX() {
        return screenPositionX;
    }

    /**
     * Sets the absolute X position of this element on the screen.
     *
     * @param screenPositionX The X position to set in pixels
     * @return This element for method chaining
     */
    public Element setScreenPositionX(float screenPositionX) {
        this.screenPositionX = screenPositionX;
        return this;
    }

    /**
     * Gets the absolute Y position of this element on the screen.
     *
     * @return The Y position in pixels
     */
    public float getScreenPositionY() {
        return screenPositionY;
    }

    /**
     * Sets the absolute Y position of this element on the screen.
     *
     * @param screenPositionY The Y position to set in pixels
     * @return This element for method chaining
     */
    public Element setScreenPositionY(float screenPositionY) {
        this.screenPositionY = screenPositionY;
        return this;
    }

    /**
     * Grows children along the width dimension based on their sizing properties.
     * This handles horizontal layout growth and vertical cross-axis growth.
     *
     * @return This element for method chaining
     */
    public Element growChildrenWidth() {
        if (direction == Direction.Horizontal) {
            growMainAxis(
                    Element::getWidth,
                    Element::setWidth,
                    Element::getxSizing,
                    Element::getMinWidth,
                    Element::getMaxWidth,
                    getWidth(),
                    paddingLeft + paddingRight
            );
        } else {
            growCrossAxis(
                    Element::getWidth,
                    Element::setWidth,
                    Element::getxSizing,
                    Element::getMinWidth,
                    Element::getMaxWidth,
                    getWidth(),
                    paddingLeft + paddingRight
            );
        }
        return this;
    }

    /**
     * Grows children along the height dimension based on their sizing properties.
     * This handles vertical layout growth and horizontal cross-axis growth.
     *
     * @return This element for method chaining
     */
    public Element growChildrenHeight() {
        if (direction == Direction.Horizontal) {
            growCrossAxis(
                    Element::getHeight,
                    Element::setHeight,
                    Element::getySizing,
                    Element::getMinHeight,
                    Element::getMaxHeight,
                    getHeight(),
                    paddingTop + paddingBottom
            );
        } else {
            growMainAxis(
                    Element::getHeight,
                    Element::setHeight,
                    Element::getySizing,
                    Element::getMinHeight,
                    Element::getMaxHeight,
                    getHeight(),
                    paddingTop + paddingBottom
            );
        }
        return this;
    }

    /**
     * Grows children along the main axis (direction of layout flow).
     * This method distributes available space among growable children
     * according to their needs, prioritizing smaller elements first.
     *
     * @param getSize    Function to get the size of an element along the main axis
     * @param setSize    Function to set the size of an element along the main axis
     * @param getSizing  Function to get the sizing strategy of an element along the main axis
     * @param parentSize The size of the parent element along the main axis
     * @param padding    The padding of the parent element along the main axis
     */
    private void growMainAxis(
            Function<Element, Float> getSize,
            BiConsumer<Element, Float> setSize,
            Function<Element, Sizing> getSizing,
            Function<Element, Float> getMinSize,
            Function<Element, Float> getMaxSize,
            float parentSize,
            float padding
    ) {

        List<Element> growable = new ArrayList<>();
        List<Element> shrinkable = new ArrayList<>();
        float remainingSpace = parentSize - padding;

        // Find growable and shrinkable children
        for (Element child : children) {
            // Fixed elements will never grow nor shrink
            if (!(getSizing.apply(child) instanceof Sizing.Fixed)) {
                growable.add(child);
                shrinkable.add(child);
            }
            remainingSpace -= getSize.apply(child);
        }

        // Account for gaps between children
        remainingSpace -= (children.size() - 1) * childGap;

        // Distribute remaining space to growable children
        while (remainingSpace > 0 && !growable.isEmpty()) {
            float smallest = getSize.apply(growable.getFirst());
            float secondSmallest = Float.POSITIVE_INFINITY;

            for (Element child : growable) {
                float size = getSize.apply(child);
                if (size < smallest) {
                    secondSmallest = smallest;
                    smallest = size;
                } else if (size > smallest && size < secondSmallest) {
                    secondSmallest = size;
                }
            }

            float sizeToAdd = Math.min(secondSmallest - smallest, remainingSpace / growable.size());

            for (Element child : growable) {
                if (getSize.apply(child) == smallest) {
                    setSize.accept(child, getSize.apply(child) + sizeToAdd);
                    remainingSpace -= sizeToAdd;
                }
                // Remove child if it has reached its maximum size
                if (getSize.apply(child) >= getMaxSize.apply(child)) {
                    setSize.accept(child, getMaxSize.apply(child));
                    growable.remove(child);
                }
            }
        }

        while (remainingSpace < 0) {
            float largest = getSize.apply(shrinkable.getFirst());
            float secondLargest = 0;

            for (Element child : shrinkable) {
                float size = getSize.apply(child);
                if (size > largest) {
                    secondLargest = largest;
                    largest = size;
                } else if (size < largest && size > secondLargest) {
                    secondLargest = size;
                }
            }

            float sizeToDrop = Math.max(secondLargest - largest, remainingSpace / shrinkable.size());

            for (Element child : growable) {
                float previousSize = getSize.apply(child);
                if (getSize.apply(child) == largest) {
                    setSize.accept(child, getSize.apply(child) + sizeToDrop);
                    if (getSize.apply(child) <= getMinSize.apply(child)) {
                        setSize.accept(child, getMinSize.apply(child));
                        growable.remove(child);
                    }
                    remainingSpace -= (getSize.apply(child) - previousSize);
                }
            }
        }
    }

    /**
     * Grows children along the cross axis (perpendicular to layout flow).
     * This will increase the size of growable children to the maximum size allowed
     * by the parent element.
     *
     * @param getSize    Function to get the size of an element along the cross axis
     * @param setSize    Function to set the size of an element along the cross axis
     * @param getSizing  Function to get the sizing strategy of an element along the cross axis
     * @param parentSize The size of the parent element along the cross axis
     * @param padding    The padding of the parent element along the cross axis
     */
    private void growCrossAxis(
            Function<Element, Float> getSize,
            BiConsumer<Element, Float> setSize,
            Function<Element, Sizing> getSizing,
            Function<Element, Float> getMinSize,
            Function<Element, Float> getMaxSize,
            float parentSize,
            float padding) {

        for (Element child : children) {
            if (!(getSizing.apply(child) instanceof Sizing.Fixed)) {
                float minSize = getMinSize.apply(child);
                float maxSize = Math.min(parentSize - padding, getMaxSize.apply(child));
                float growthDistance = minSize + (maxSize - minSize);
                setSize.accept(child, growthDistance);
            }
        }
    }

    /**
     * Gets the minimum width constraint of this element.
     *
     * @return The minimum width in pixels
     */
    public float getMinWidth() {
        return minWidth;
    }

    /**
     * Sets the minimum width constraint of this element.
     *
     * @param minWidth The minimum width to set in pixels
     * @return This element for method chaining
     */
    public Element setMinWidth(float minWidth) {
        this.minWidth = minWidth;
        return this;
    }

    /**
     * Gets the minimum height constraint of this element.
     *
     * @return The minimum height in pixels
     */
    public float getMinHeight() {
        return minHeight;
    }

    /**
     * Sets the minimum height constraint of this element.
     *
     * @param minHeight The minimum height to set in pixels
     * @return This element for method chaining
     */
    public Element setMinHeight(float minHeight) {
        this.minHeight = minHeight;
        return this;
    }

    /**
     * Gets the maximum width constraint of this element.
     *
     * @return The maximum width in pixels
     */
    public float getMaxWidth() {
        return maxWidth;
    }

    /**
     * Sets the maximum width constraint of this element.
     *
     * @param maxWidth The maximum width to set in pixels
     * @return This element for method chaining
     */
    public Element setMaxWidth(float maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }

    /**
     * Gets the maximum height constraint of this element.
     *
     * @return The maximum height in pixels
     */
    public float getMaxHeight() {
        return maxHeight;
    }

    /**
     * Sets the maximum height constraint of this element.
     *
     * @param maxHeight The maximum height to set in pixels
     * @return This element for method chaining
     */
    public Element setMaxHeight(float maxHeight) {
        this.maxHeight = maxHeight;
        return this;
    }

    /**
     * Gets the horizontal alignment of children within this element.
     *
     * @return The horizontal alignment (Start, Center, or End)
     */
    public Alignment getxAlign() {
        return xAlign;
    }

    /**
     * Sets the horizontal alignment pattern of the children of this element.
     *
     * @param xAlign The horizontal alignment to set (Start, Center, or End)
     */
    public Element setxAlign(Alignment xAlign) {
        this.xAlign = xAlign;
        return this;
    }

    /**
     * Gets the vertical alignment of children within this element.
     *
     * @return The vertical alignment (Start, Center, or End)
     */
    public Alignment getyAlign() {
        return yAlign;
    }

    /**
     * Sets the vertical alignment pattern of the children of this element.
     *
     * @param yAlign The vertical alignment to set (Start, Center, or End)
     */
    public Element setyAlign(Alignment yAlign) {
        this.yAlign = yAlign;
        return this;
    }
}
