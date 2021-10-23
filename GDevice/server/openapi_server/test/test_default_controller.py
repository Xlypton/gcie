# coding: utf-8

from __future__ import absolute_import
import unittest

from flask import json
from six import BytesIO

from openapi_server.models.control_data import ControlData  # noqa: E501
from openapi_server.test import BaseTestCase


class TestDefaultController(BaseTestCase):
    """DefaultController integration test stubs"""

    def test_control_get(self):
        """Test case for control_get

        
        """
        headers = { 
            'Accept': 'application/json',
        }
        response = self.client.open(
            '/control',
            method='GET',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_control_post(self):
        """Test case for control_post

        
        """
        control_data = {
  "timeStamp" : "2021-10-20T19:32:28.345Z",
  "slider" : 24,
  "select" : [ true, true, true, true ],
  "rotaryKnob" : 60,
  "switch" : true
}
        headers = { 
            'Content-Type': 'application/json',
        }
        response = self.client.open(
            '/control',
            method='POST',
            headers=headers,
            data=json.dumps(control_data),
            content_type='application/json')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))


if __name__ == '__main__':
    unittest.main()
