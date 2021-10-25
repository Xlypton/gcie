import queue
import connexion
import six
from queue import Queue

from openapi_server.models.control_data import ControlData  # noqa: E501
from openapi_server import util
from openapi_server import gqueue


def control_get():  # noqa: E501
    """control_get

     # noqa: E501


    :rtype: ControlData
    """
    return 'WORKED!'


def control_post(control_data):  # noqa: E501
    """control_post

    This operation allows for sending control data. # noqa: E501

    :param control_data: 
    :type control_data: dict | bytes

    :rtype: None
    """
    if connexion.request.is_json:
        control_data = ControlData.from_dict(connexion.request.get_json())  # noqa: E501

        gqueue.results_queue.put("control_data")

      
    return 'worked3'
