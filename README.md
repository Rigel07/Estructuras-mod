# Cute Realms (mod para Minecraft Forge 1.20.1)

Cuatro estructuras con una función real, cada una con sus habitantes muy cute, y los objetos que las conectan.

## Subirlo a GitHub (se descomprime y compila solo)
1. Crea un repositorio nuevo en GitHub.
2. *Add file > Create new file*, escribe el nombre `.github/workflows/build.yml` (al escribir `/` se crean las carpetas)
   y pega dentro el contenido del archivo **build.yml** que viene aparte. Commit.
3. *Add file > Upload files* y arrastra el **.zip tal cual** (sin descomprimir). Commit.
4. Pestaña **Actions**: la ejecución extrae el zip, guarda el código en el repositorio y compila.
   Al terminar, entra en ella y descarga el artefacto **mod-jar** (dentro está el .jar para la carpeta `mods`
   de Forge 1.20.1 o para CurseForge).
   Si quieres volver a extraer el zip: Actions > el workflow > *Run workflow* > marca "reextraer".

## Las estructuras y qué hacen
| Estructura | Dónde | Función | Habitantes |
|---|---|---|---|
| **Panadería Mochi** | llanuras, prados, bosques, cerezos | Tienda: compras comida cute, monedas de deseo y vendes ingredientes. Cocina con hornos y ahumadores, despensas y un altillo con cofre. | **Mochi** (panadero, comercia) |
| **Observatorio Estelar** | colinas, montañas, llanuras | Biblioteca con mesa de cartografía y telar; planta de encantamiento con estanterías; **telescopio** en la cúpula; allí vive el vendedor de **mapas** a las otras estructuras. | **Estrellín** (astrónomo, comercia) |
| **Santuario de la Fuente** | bosques, cerezos, taiga | **Fuente de los deseos** (tira una moneda y recibe botín), **aura curativa** de las hadas, pabellones con ofrendas y **cripta secreta** bajo la plaza con tesoro. | **Lumi** (hadas voladoras) |
| **Refugio de Pompoms** | llanuras, sabana, taiga | **Adopción**: corrales con Pompoms que se doman, despensa de comida, tienda de correas/etiquetas/chuches. | **Tuli** (cuidadora, comercia) y **Pompoms** |

## Mobs
- **Mochi / Estrellín / Tuli**: tenderos invulnerables, ofertas que se renuevan cada día. Estrellín vende mapas de exploración
  (panadería, santuario, refugio). Tuli vende el huevo de Pompom (adopción).
- **Lumi**: hada que vuela por el santuario. Cura (regeneración) a quien se acerque. Dale bayas dulces o luminosas
  y te regala polvo de hada de vez en cuando.
- **Pompom**: 6 colores. Se doma con chuche de pompom (seguro) o zanahoria/bayas (1 de cada 3). Clic derecho con la mano vacía:
  se queda quieto / te sigue. Su pelusa de la suerte te da **Suerte** mientras esté cerca (¡mejora los deseos de la fuente!).
  Se cría con zanahorias.

## Objetos y bloques
- **Telescopio** (crafteable: 4 lingotes de cobre, 1 catalejo y 1 polvo de estrellas): de noche, clic derecho y te dice
  hacia dónde y a cuántos bloques está la panadería, el santuario o el refugio más cercano.
- **Fuente de los deseos** (solo creativo): clic derecho con **moneda de deseo** o **pepita de oro**. Usa tu Suerte, y 1 de cada 50 es un "gran deseo".
- **Mochi** (regeneración), **Mochi de fresa** (absorción), **Té de burbujas** (velocidad y prisa).
- **Moneda de deseo**: se vende en la panadería o se crafta (pepita de oro + polvo de hada = 2 monedas).
- **Polvo de hada**: caída lenta y salto alto un buen rato. **Polvo de estrellas**: amatista + polvo de piedra luminosa = 2.
- **Chuche de pompom**: zanahoria + bayas dulces + azúcar = 2.

## Probarlo rápido en el juego
- `/place structure cute_realms:mochi_bakery` (también `star_observatory`, `wishing_sanctuary`, `pompom_shelter`)
- `/locate structure cute_realms:wishing_sanctuary`
- Pestaña creativa **Cute Realms**: huevos de todos los mobs, objetos, telescopio y fuente.

## Dónde ajustar cosas
- `data/cute_realms/worldgen/structure_set/*.json`: frecuencia (spacing/separation).
- `data/cute_realms/tags/worldgen/biome/has_*.json`: biomas de cada estructura.
- `data/cute_realms/loot_tables/chests/*.json` y `gameplay/*.json`: botín de cofres y de la fuente de los deseos.
- `entity/CuteMerchantEntity.java`: ofertas de cada tendero (`updateTrades`).
- `data/cute_realms/structures/**.nbt`: los edificios (se pueden abrir con bloques de estructura en creativo).
