import json, os
from PIL import Image, ImageDraw

ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "src", "main", "resources",
                    "assets", "minecraft", "textures", "gui")
SPR = os.path.join(ROOT, "sprites", "widget")
CON = os.path.join(ROOT, "container")
os.makedirs(SPR, exist_ok=True)
os.makedirs(CON, exist_ok=True)

DARK = (30, 30, 31, 255)

def button(name, fill, top, left, right, mid, bottom):
    g = lambda v: (v, v, v, 255)
    im = Image.new("RGBA", (200, 20), g(fill))
    d = ImageDraw.Draw(im)
    d.rectangle([0, 0, 199, 0], fill=g(top))
    d.rectangle([0, 0, 0, 19], fill=g(left))
    d.rectangle([199, 1, 199, 19], fill=g(right))
    d.rectangle([1, 17, 198, 18], fill=g(mid))
    d.rectangle([0, 19, 199, 19], fill=g(bottom))
    im.save(os.path.join(SPR, name + ".png"))
    with open(os.path.join(SPR, name + ".png.mcmeta"), "w") as f:
        json.dump({"gui": {"scaling": {"type": "nine_slice", "width": 200, "height": 20, "border": 3}}}, f)

button("button", 198, 222, 214, 170, 139, 58)
button("button_highlighted", 212, 255, 255, 200, 150, 60)
button("button_disabled", 150, 170, 165, 130, 110, 70)

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

# Hide the "Java Edition" subtitle under the logo (Bedrock has none)
os.makedirs(os.path.join(ROOT, "title"), exist_ok=True)
Image.new("RGBA", (128, 14), (0, 0, 0, 0)).save(os.path.join(ROOT, "title", "edition.png"))
