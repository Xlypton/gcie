import connexion
import six

from openapi_server.models.control_data import ControlData  # noqa: E501
from openapi_server import util


def control_get():  # noqa: E501
    """control_get

     # noqa: E501


    :rtype: ControlData
    """
    return 'do some magic!'


def control_post(control_data):  # noqa: E501
    """control_post

    This operation allows for sending control data. # noqa: E501

    :param control_data: 
    :type control_data: dict | bytes

    :rtype: None
    """
    if connexion.request.is_json:
        control_data = ControlData.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'
