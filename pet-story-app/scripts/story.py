#!/usr/bin/env python3
from transformers import pipeline
import sys

prompt = sys.argv[1]

generator = pipeline("text-generation", model="gpt2")
result = generator(prompt, max_length=100)[0]["generated_text"]
print(result)
