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

button("button", 198, 208, 198, 182, 139, 139)
button("button_highlighted", 212, 232, 212, 196, 150, 150)
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

# ---- Mod icon (128x128): window with two Bedrock-style buttons ----
ICON_DIR = os.path.join(ROOT, "..", "..", "..", "bedrockgui")
os.makedirs(ICON_DIR, exist_ok=True)

def gr(v):
    return (v, v, v, 255)

def bevel(dr, x0, y0, x1, y1, fill=198):
    dr.rectangle([x0, y0, x1, y1], fill=gr(fill))
    dr.rectangle([x0, y0, x1, y0 + 2], fill=gr(235))
    dr.rectangle([x0, y0, x0 + 2, y1], fill=gr(222))
    dr.rectangle([x1 - 2, y0, x1, y1], fill=gr(170))
    dr.rectangle([x0, y1 - 5, x1, y1 - 1], fill=gr(139))
    dr.rectangle([x0, y1 - 1, x1, y1], fill=gr(58))

ic = Image.new("RGBA", (128, 128))
dr = ImageDraw.Draw(ic)
dr.rectangle([0, 0, 127, 127], fill=(70, 120, 190, 255))
for yy in range(0, 128, 2):
    dr.line([0, yy, 127, yy], fill=(70 + yy // 6, 120 + yy // 8, 190, 255))
dr.rectangle([10, 14, 117, 114], fill=gr(30))
dr.rectangle([12, 16, 115, 112], fill=gr(198))
dr.rectangle([12, 16, 115, 34], fill=gr(80))
dr.rectangle([20, 22, 60, 28], fill=gr(230))
bevel(dr, 24, 46, 103, 68)
bevel(dr, 24, 78, 103, 100)
dr.rectangle([48, 54, 79, 58], fill=gr(80))
dr.rectangle([48, 86, 79, 90], fill=gr(80))
ic.save(os.path.join(ICON_DIR, "icon.png"))
