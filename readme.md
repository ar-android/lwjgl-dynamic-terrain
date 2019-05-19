# Dynamic Terrain Generator

CSI4341 Computer Graphics Final Project

![intro image - view of terrain](https://bitbucket.org/antoninsmid/dynamic-terrain-generator-project/raw/560eb8a850a97441805bcd34461c4d5bd7d92c2d/introimg.png)

[Short video walkthrough](https://youtu.be/amdQcXa1zlU)

Terrain Generator based on Lightweight Java Game Lubrary and Fast Noise Library. The demo produces 3D terrain with grass, trees, rocks and lakes.
The player can either fly around or use the first person view to enjoy the details on the ground.

## Getting Started

### Prerequisites

To run this project you will need Java 8 and higher.

### Installing

- Download this repository
- Open as new project in Eclipse
- Add the libraries from /lib/jars to your project (Properties -> Java Build Path -> Libraries -> ClassPath -> ADD JARs)
- Select the natives based on your operating system from /lib/natives/{mac, linux, windows} (... -> Libraries -> ModulePath -> JRE System Library -> Native Libray Location -> Edit)

## Running

Run/src/renderEngineTest/MainGameLoop.java

## Controlls

There are two camera movement modes Free Flight and Walk, you switch between them with the **P** key.

### Free Flight

Use **W S A D** to move horizontally, **Q E** to move vertically, **I J** to change speed. And mouse to look around. The demo will catch your cursor on click. To break from the camera movement press **ESC**.

### Walk

To use walk camera control, switch with **P** key and move **W S A D**.

## Implemented features

- custom OBJ loader
- terrain generation based on noise
- dynamic terrain detail based on viewing distance
- textures ont errain based on terrain height
- tiling of the textures change and blends based on viewing distance
- massive multithreaded object spawning
- dissolve appear/ disappear to smooth the transitions
- LODs
- fake normals based on terrain normal - for foliage rendering
- binary transparency - for foliage
- skybox
- water reflection, refraction
- bump, specular, DUDV animated maps on water surface
- water depth, fresnel effect
- free flight camera
- first person walk camera

## Built With

- [Lightweight Java Game Library](https://www.lwjgl.org/) - The web framework used
- [Slick](https://github.com/ariejan/slick2d/tree/master/src/org/newdawn/slick) - Texture Loadgind library
- [Fast Noise](https://github.com/Auburns/FastNoise_Java) - Used to generate Noise for terrain hight maps
- [Skybox](http://mi.eng.cam.ac.uk/~agk34/resources/textures/sky/) - Cube Map for Skybox
- [Textures.com](https://www.textures.com/) - Texture source
- [BlendSwap.com](https://www.blendswap.com/) - Texture source

## License

This project is licensed under the MIT License.
