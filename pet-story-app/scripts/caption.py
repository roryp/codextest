#!/usr/bin/env python3
from transformers import pipeline
from PIL import Image
import sys

captioner = pipeline("image-to-text", model="Salesforce/blip-image-captioning-base")
image = Image.open(sys.argv[1])
result = captioner(image)[0]["generated_text"]
print(result)
