from kivy.app import App
from kivy.clock import mainthread
from kivy.uix.widget import Widget
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.stacklayout import StackLayout
from kivy.properties import StringProperty
from threading import Thread

import connexion

from GDevice.gserver.src.openapi_server.uvicorn_runner import run_fastapi

class GDevice(App):
    pass

class MainScreen(BoxLayout):

    switch_value = StringProperty("N/A")
    
    #def __init__(self, switch_value="N/A", **kwargs):
     #   super(MainScreen, self).__init__(**kwargs)
      #  self.switch_value = switch_value

    def on_server_button_click(self):
        Thread(target=run_fastapi).start()


GDevice().run()