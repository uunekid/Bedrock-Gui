import json, os, random
from PIL import Image, ImageDraw

ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "src", "main", "resources",
                    "assets", "minecraft", "textures", "gui")
SPR = os.path.join(ROOT, "sprites", "widget")
CON = os.path.join(ROOT, "container")
os.makedirs(SPR, exist_ok=True)
os.makedirs(CON, exist_ok=True)

DARK = (30, 30, 31, 255)

def button(name, fill, light, shadow, outline=DARK):
    im = Image.new("RGBA", (200, 20), (0, 0, 0, 0))
    d = ImageDraw.Draw(im)
    d.rectangle([0, 0, 199, 19], fill=outline)
    d.rectangle([1, 1, 198, 18], fill=fill)
    d.rectangle([1, 1, 198, 1], fill=light)
    d.rectangle([1, 1, 1, 18], fill=light)
    d.rectangle([1, 17, 198, 18], fill=shadow)
    d.rectangle([198, 2, 198, 18], fill=shadow)
    im.save(os.path.join(SPR, name + ".png"))
    with open(os.path.join(SPR, name + ".png.mcmeta"), "w") as f:
        json.dump({"gui": {"scaling": {"type": "nine_slice", "width": 200, "height": 20, "border": 3}}}, f)

button("button", (198, 198, 198, 255), (222, 222, 222, 255), (138, 138, 138, 255))
button("button_highlighted", (214, 214, 214, 255), (240, 240, 240, 255), (156, 156, 156, 255),
       outline=(255, 255, 255, 255))
button("button_disabled", (120, 120, 120, 255), (140, 140, 140, 255), (92, 92, 92, 255))

im = Image.new("RGBA", (256, 256), (0, 0, 0, 0))
d = ImageDraw.Draw(im)
d.rectangle([0, 0, 175, 165], fill=DARK)
d.rectangle([1, 1, 174, 164], fill=(198, 198, 198, 255))
d.rectangle([1, 1, 174, 1], fill=(235, 235, 235, 255))
d.rectangle([1, 1, 1, 164], fill=(235, 235, 235, 255))
d.rectangle([2, 163, 174, 164], fill=(138, 138, 138, 255))
d.rectangle([174, 2, 174, 164], fill=(138, 138, 138, 255))

def slot(x, y, size=18):
    x0, y0 = x - 1, y - 1
    d.rectangle([x0, y0, x0 + size - 1, y0 + size - 1], fill=(139, 139, 139, 255))
    d.rectangle([x0, y0, x0 + size - 1, y0], fill=(55, 55, 55, 255))
    d.rectangle([x0, y0, x0, y0 + size - 1], fill=(55, 55, 55, 255))
    d.rectangle([x0, y0 + size - 1, x0 + size - 1, y0 + size - 1], fill=(255, 255, 255, 255))
    d.rectangle([x0 + size - 1, y0, x0 + size - 1, y0 + size - 1], fill=(255, 255, 255, 255))

for i in range(3):
    for j in range(3):
        slot(30 + j * 18, 17 + i * 18)
d.rectangle([119, 30, 144, 55], fill=(139, 139, 139, 255))
d.rectangle([119, 30, 144, 30], fill=(55, 55, 55, 255))
d.rectangle([119, 30, 119, 55], fill=(55, 55, 55, 255))
d.rectangle([119, 55, 144, 55], fill=(255, 255, 255, 255))
d.rectangle([144, 30, 144, 55], fill=(255, 255, 255, 255))
for i in range(3):
    for j in range(9):
        slot(8 + j * 18, 84 + i * 18)
for j in range(9):
    slot(8 + j * 18, 142)

a = (80, 80, 82, 255)
d.rectangle([90, 40, 104, 46], fill=a)
d.polygon([(104, 36), (104, 50), (112, 43)], fill=a)

im.save(os.path.join(CON, "crafting_table.png"))
print("textures written")

# ---- Dark brick panorama (Bedrock-like dark background) ----
BG = os.path.join(ROOT, "title", "background")
os.makedirs(BG, exist_ok=True)
SIZE, BW, BH, MORTAR = 512, 64, 32, 3
rng = random.Random(7)
rows = SIZE // BH
cols = SIZE // BW
colors = []
for r in range(rows):
    row = []
    for c in range(cols):
        if rng.random() < 0.12:
            base = (96 + rng.randint(-10, 10), 58 + rng.randint(-8, 8), 42 + rng.randint(-6, 6))
        else:
            v = rng.randint(-8, 8)
            base = (44 + v, 56 + v, 50 + v)
        row.append(base)
    colors.append(row)
tile = Image.new("RGB", (SIZE, SIZE))
px = tile.load()
for y in range(SIZE):
    r = y // BH
    off = (BW // 2) if r % 2 else 0
    for x in range(SIZE):
        xx = (x + off) % SIZE
        c = xx // BW
        inx = xx % BW
        iny = y % BH
        if inx < MORTAR or iny < MORTAR:
            col = (20, 26, 24)
        else:
            b = colors[r][c]
            n = rng.randint(-5, 5)
            col = (b[0] + n, b[1] + n, b[2] + n)
        px[x, y] = col
for i in range(6):
    tile.save(os.path.join(BG, "panorama_%d.png" % i))

# ---- Hide the "Java Edition" subtitle under the logo ----
Image.new("RGBA", (128, 14), (0, 0, 0, 0)).save(os.path.join(ROOT, "title", "edition.png"))
print("title textures written")
