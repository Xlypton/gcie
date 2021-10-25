# coding: utf-8

from typing import Dict, List  # noqa: F401

from fastapi import (  # noqa: F401
    APIRouter,
    Body,
    Cookie,
    Depends,
    Form,
    Header,
    Path,
    Query,
    Response,
    Security,
    status,
)

from openapi_server.models.extra_models import TokenModel  # noqa: F401
from openapi_server.models.control_data import ControlData


router = APIRouter()


@router.get(
    "/control",
    responses={
        200: {"model": ControlData, "description": "The current state of the stored values."},
        400: {"description": "Invalid imput."},
        500: {"description": "Internal server error."},
    },
    tags=["default"],
)
async def control_get(
) -> ControlData:
    ...


@router.post(
    "/control",
    responses={
        204: {"description": "The sent control was succesfuly processed."},
        400: {"description": "Invalid imput."},
        500: {"description": "Internal server error."},
    },
    tags=["default"],
)
async def control_post(
    control_data: ControlData = Body(None, description=""),
) -> None:
    """This operation allows for sending control data."""
    ...
