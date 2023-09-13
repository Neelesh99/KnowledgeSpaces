import shutil
from pathlib import Path
from tempfile import NamedTemporaryFile

from fastapi import UploadFile


def plain_text_handler(data: bytes):
    return data.decode()

def link_handler(data: bytes):
    return data.decode()

async def save_upload_file_tmp(upload_file: UploadFile) -> Path:
    try:
        suffix = Path(upload_file.filename).suffix
        with NamedTemporaryFile(delete=False, suffix=suffix) as tmp:

            read = await upload_file.read()
            tmp.write(read)
            tmp_path = Path(tmp.name)
    finally:
        upload_file.file.close()
    return tmp_path