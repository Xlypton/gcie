from . import main
import uvicorn

def run_fastapi():
    uvicorn.run(main.app, host="0.0.0.0", port=5000, log_level="info")