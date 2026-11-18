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
hl = Image.new("RGBA", (200, 20), (32, 131, 5, 255))
h = ImageDraw.Draw(hl)
h.rectangle([0, 0, 199, 19], outline=(245, 250, 245, 255))
h.rectangle([1, 1, 198, 1], fill=(110, 215, 118, 255))
h.rectangle([1, 2, 1, 18], fill=(67, 181, 57, 255))
h.rectangle([198, 2, 198, 18], fill=(10, 90, 2, 255))
h.rectangle([2, 17, 197, 18], fill=(20, 74, 22, 255))
hl.save(os.path.join(SPR, "button_highlighted.png"))
with open(os.path.join(SPR, "button_highlighted.png.mcmeta"), "w") as f:
    json.dump({"gui": {"scaling": {"type": "nine_slice", "width": 200, "height": 20, "border": 3}}}, f)
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

# ---- Sliders (Options screens): Bedrock-style track and handle ----
def sprite(name, width, fill, top, left, right, mid, bottom, edge=None):
    def c(v):
        return (v, v, v, 255) if isinstance(v, int) else tuple(v) + (255,)
    sl = Image.new("RGBA", (width, 20), c(fill))
    sd = ImageDraw.Draw(sl)
    sd.rectangle([0, 0, width - 1, 0], fill=c(top))
    sd.rectangle([0, 0, 0, 19], fill=c(left))
    sd.rectangle([width - 1, 1, width - 1, 19], fill=c(right))
    sd.rectangle([1, 17, width - 2, 18], fill=c(mid))
    sd.rectangle([0, 19, width - 1, 19], fill=c(bottom))
    if edge:
        sd.rectangle([0, 0, width - 1, 19], outline=c(edge))
    sl.save(os.path.join(SPR, name + ".png"))
    with open(os.path.join(SPR, name + ".png.mcmeta"), "w") as mf:
        json.dump({"gui": {"scaling": {"type": "nine_slice", "width": width, "height": 20, "border": 3}}}, mf)

sprite("slider", 200, 150, 96, 110, 176, 176, 200)
sprite("slider_highlighted", 200, (32, 131, 5), (20, 74, 22), (20, 90, 10), (67, 181, 57),
       (67, 181, 57), (110, 215, 118), edge=(245, 250, 245))
sprite("slider_handle", 8, 198, 235, 222, 170, 139, 58)
sprite("slider_handle_highlighted", 8, 225, 255, 255, 190, 160, 70)
# ---- Sidebar icons for the settings screen (16x16) ----
ICON_OUT = os.path.join(ROOT, "..", "..", "..", "bedrockgui", "textures", "gui")
os.makedirs(ICON_OUT, exist_ok=True)

def new_icon(bg, border=(25, 25, 28)):
    ic = Image.new("RGBA", (16, 16), bg + (255,))
    idr = ImageDraw.Draw(ic)
    idr.rectangle([0, 0, 15, 15], outline=border + (255,))
    return ic, idr

ic, idr = new_icon((70, 45, 30))
for (cx, cy) in [(7, 2), (7, 12), (2, 7), (12, 7), (3, 3), (11, 3), (3, 11), (11, 11)]:
    idr.rectangle([cx, cy, cx + 2, cy + 2], fill=(222, 128, 40, 255))
idr.ellipse([3, 3, 12, 12], fill=(238, 150, 55, 255))
idr.ellipse([6, 6, 9, 9], fill=(70, 45, 30, 255))
ic.save(os.path.join(ICON_OUT, "icon_general.png"))

ic, idr = new_icon((88, 88, 90))
idr.rectangle([4, 2, 11, 13], fill=(200, 200, 202, 255), outline=(40, 40, 42, 255))
idr.rectangle([7, 3, 8, 6], fill=(90, 90, 92, 255))
idr.line([4, 7, 11, 7], fill=(40, 40, 42, 255))
ic.save(os.path.join(ICON_OUT, "icon_controls.png"))

ic, idr = new_icon((40, 40, 44))
idr.rectangle([1, 1, 14, 8], fill=(100, 180, 240, 255))
idr.rectangle([1, 9, 14, 14], fill=(84, 168, 60, 255))
idr.rectangle([2, 2, 4, 4], fill=(255, 255, 255, 255))
idr.polygon([(9, 9), (12, 5), (14, 9)], fill=(70, 140, 50, 255))
ic.save(os.path.join(ICON_OUT, "icon_video.png"))

ic, idr = new_icon((120, 62, 32))
for gx in range(1, 15):
    for gy in range(1, 15):
        if (gx + gy) % 2 == 0:
            idr.point((gx, gy), fill=(176, 96, 52, 255))
ic.save(os.path.join(ICON_OUT, "icon_audio.png"))

ic, idr = new_icon((50, 84, 150))
idr.rectangle([2, 3, 13, 10], fill=(240, 240, 244, 255))
idr.polygon([(4, 11), (4, 14), (8, 11)], fill=(240, 240, 244, 255))
for lx in (4, 7, 10):
    idr.rectangle([lx, 6, lx + 1, 7], fill=(50, 84, 150, 255))
ic.save(os.path.join(ICON_OUT, "icon_chat.png"))

ic, idr = new_icon((40, 92, 235))
idr.rectangle([6, 2, 9, 5], fill=(255, 255, 255, 255))
idr.rectangle([7, 6, 8, 9], fill=(255, 255, 255, 255))
idr.rectangle([3, 6, 12, 7], fill=(255, 255, 255, 255))
idr.rectangle([5, 10, 6, 13], fill=(255, 255, 255, 255))
idr.rectangle([9, 10, 10, 13], fill=(255, 255, 255, 255))
ic.save(os.path.join(ICON_OUT, "icon_access.png"))
# ---- Thin Bedrock-style slider track and a clear handle ----
def thin_track(name, track, top, bottom):
    t = Image.new("RGBA", (200, 20), (0, 0, 0, 0))
    td = ImageDraw.Draw(t)
    td.rectangle([0, 7, 199, 12], fill=(track, track, track, 255))
    td.rectangle([0, 7, 199, 7], fill=(top, top, top, 255))
    td.rectangle([0, 12, 199, 12], fill=(bottom, bottom, bottom, 255))
    t.save(os.path.join(SPR, name + ".png"))
    with open(os.path.join(SPR, name + ".png.mcmeta"), "w") as tf:
        json.dump({"gui": {"scaling": {"type": "nine_slice", "width": 200, "height": 20, "border": 3}}}, tf)

def handle(name, fill, edge):
    hd = Image.new("RGBA", (8, 20), (fill, fill, fill, 255))
    hdd = ImageDraw.Draw(hd)
    hdd.rectangle([0, 0, 7, 19], outline=edge + (255,))
    hdd.rectangle([1, 1, 6, 1], fill=(240, 240, 240, 255))
    hdd.rectangle([1, 16, 6, 18], fill=(139, 139, 139, 255))
    hd.save(os.path.join(SPR, name + ".png"))
    with open(os.path.join(SPR, name + ".png.mcmeta"), "w") as hf:
        json.dump({"gui": {"scaling": {"type": "nine_slice", "width": 8, "height": 20, "border": 3}}}, hf)

thin_track("slider", 150, 100, 178)
thin_track("slider_highlighted", 172, 120, 200)
handle("slider_handle", 198, (30, 30, 31))
handle("slider_handle_highlighted", 225, (255, 255, 255))
