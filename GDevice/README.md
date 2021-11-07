# GDevice
## Description
**GDevice** is simple python **desktop app** and a **web server**. The UI is built using the Kivy UI framework. The server side code uses Connexion and generated using openapi-generator. The app is able to display and store values sent by a controller mobile app. 

## Tools/Libraries
### OpenAPI
Openapi or the Openapi Specification (OAS), defines a standard language agnostic approach to developing RESTful APIs, which are both human and machine readable.

### Swagger
A set of open-source tools built around the OAS that help support development, including:
- Swagger Editor: Browser based editor where you can write (and view) OpenAPI specs.
- Swagger UI: Renders OAS as interactive API documentation (also can be seen within Swagger Editor).
- Swagger Codegen - generates server stubs and client libraries from an OpenAPI spec.

### FastAPI
FastAPI is a modern, fast (high-performance), web framework for building APIs with Python 3.6+ based on standard Python type hints. This project runs the FastAPI application in a remote server machine using **Uvicorn**

### Uvicorn
Uvicorn is a lightning-fast ASGI server implementation, using uvloop and httptools.

## Installation
Loading project dependencies:
```
pip3 install -r requirements.txt
```

## API generation
```
java -jar openapi-generator-cli.jar generate -g python-fastapi -i ../Api/openapi:control.yml -o gserver/
```

## Run the App
```
cd ui/
python main.py
```

