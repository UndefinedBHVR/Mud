package com.undefinedbhvr.mud.layout;

public sealed interface Sizing permits Sizing.Fixed, Sizing.Grow, Sizing.Hug {
    int getMin();
    int getMax();

    /**
     * Fixed size in pixels
     */
    final class Fixed implements Sizing {
        private final int min;
        private final int max;

        public Fixed(int pixels) {
            this.min = pixels;
            this.max = pixels;
        }

        @Override
        public int getMin() {
            return min;
        }

        @Override
        public int getMax() {
            return max;
        }
    }

    /**
     * Grow to fill available space
     */
    final class Grow implements Sizing {
        private final int min;
        private final int max;
        public Grow() {
            this.min = 0;
            this.max = Integer.MAX_VALUE;
        }

        public Grow(int min) {
            this.min = min;
            this.max = Integer.MAX_VALUE;
        }

        public Grow(int min, int max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public int getMin() {
            return min;
        }

        @Override
        public int getMax() {
            return max;
        }
    }

    /**
     * Size set to the size of the largest child in either direction
     */

    final class Hug implements Sizing {
        private final int min;
        private final int max;

        public Hug() {
            this.min = 0;
            this.max = Integer.MAX_VALUE;
        }

        public Hug(int min) {
            this.min = min;
            this.max = Integer.MAX_VALUE;
        }

        public Hug(int min, int max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public int getMin() {
            return min;
        }

        @Override
        public int getMax() {
            return max;
        }
    }
}