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

### Connexion
Is a Python library that "automagically" handles HTTP requests based on your OAS. It acts as a simple wrapper around Flask reducing the boilerplate code you have to write as well.

### FastAPI
FastAPI is a modern, fast (high-performance), web framework for building APIs with Python 3.6+ based on standard Python type hints.

## Developmet
### Installation
The project uses virtual **environment** to activate it use:  
(on mac)
```
source venv/bin/activate
```
(on windows)
```
venv/bin/activate
```
Loading project dependencies:
```
pip install -r requirements.txt
```
