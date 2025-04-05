# Mud
Mud is a UI layout library for Minecraft, based heavily on the layout algorithm used by the C Library 
[Clay](https://github.com/nicbarker/clay)

## Features
- **[ Responsive Layout ]**: The Clay layout algorithm used by Mud is fully responsive and capable of adapting to almost every screen size available.


- **[ Realtime Layout Creation ]**: Mud is built to be as performant as possible, and should be able to generate layouts in less than a millisecond in most cases, or your money back guaranteed!


- **[ Simple API ]**: The API for Mud is designed to be as simple and intuitive as possible, to the extent where even a web developer can use it (wink).

## Implementation Details
Mud uses a multi-pass layout algorithm. The final algorithm will use the following seven passes:
  - The First pass is the initial layout creation, which collects elements by Depth First Post-Order into a List.
  - The Second pass is Fixed Width Sizing, which calculates the size along the X axis for all elements following Depth First Post-Order ordering.
  - The Third pass is Width Growth/Shrink Sizing, which grows/shrinks relevant items along the X axis to fill or fit within its parent element following Depth First Post-Order ordering.
  - The Fourth pass is Text Wrapping, which is currently unimplemented.
  - The Fifth pass is Fixed Height Sizing, which calculates the size along the Y axis for all elements following Depth First Post-Order ordering.
  - The Sixth pass is Height Growth/Shrink Sizing, which grows/shrinks relevant items along the Y axis to fill or fit within its parent element following Depth First Post-Order ordering.
  - The Seventh pass is Positioning/Alignment which uses the calculated sizes and parent's alignment rules to determine the placement of elements along each axis.

## Disclaimer
The code contained within this repository is an original implementation of the same layout algorithm used by the Clay UI Layout Library.

While none of the code contained within is directly based nor transliterated from the original implementation within Clay's source code, it is based on the same algorithm, and as such should be considered licensed under the original terms defined within Clay's source code.

This exact implementation is based solely off the following video which was released by the original author of Clay, [Nic Barker](https://github.com/nicbarker).

[![YouTube](http://i.ytimg.com/vi/by9lQvpvMIc/hqdefault.jpg)](https://www.youtube.com/watch?v=by9lQvpvMIc)

## License
The algorithm used by this library is licensed from Nic Barker under the `zlib/libpng license`. The original license may be viewed in the [LICENSE](https://github.com/nicbarker/clay/blob/main/LICENSE.md) file in its relevant repository. Please see the [Disclaimer](#Disclaimer) section for more information.

The source form representation implement within this repository is licensed under the ISC License. Please see the [LICENSE](LICENSE.txt) file for details.