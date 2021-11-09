# coding: utf-8

from fastapi.testclient import TestClient


from openapi_server.models.control_data import ControlData  # noqa: F401


def test_control_get(client: TestClient):
    """Test case for control_get

    
    """

    headers = {
    }
    response = client.request(
        "GET",
        "/control",
        headers=headers,
    )

    # uncomment below to assert the status code of the HTTP response
    #assert response.status_code == 200


def test_control_post(client: TestClient):
    """Test case for control_post

    
    """
    control_data = {"time_stamp":"2021-10-20T19:32:28.345Z","slider":24,"select":[1,1,1,1],"rotary_knob":60,"switch":1}

    headers = {
    }
    response = client.request(
        "POST",
        "/control",
        headers=headers,
        json=control_data,
    )

    # uncomment below to assert the status code of the HTTP response
    #assert response.status_code == 200

