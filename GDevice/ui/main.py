import multiprocessing
from GDevice.gserver.openapi_server.apis.g_api import run_server
from GDevice.gserver.openapi_server.models.control_data import ControlData

def start_server(child_conn: multiprocessing.Pipe):
    global server_proc
    server_proc = multiprocessing.Process(target=run_server, args=(child_conn,), daemon=True)
    server_proc.start()

def stop_server(): 
    server_proc.terminate()

if __name__ == '__main__':
    multiprocessing.freeze_support()
    from kivy.app import App
    from kivy.uix.widget import Widget
    from kivy.uix.button import Button
    from kivy.uix.boxlayout import BoxLayout
    from kivy.uix.stacklayout import StackLayout
    from kivy.properties import StringProperty
    from kivy.properties import BooleanProperty
    from kivy.clock import Clock
    from kivy.core.window import Window

    class GDevice(App):
        def build(self):
            Window.clearcolor = (30/255, 43/255, 51/255, 1)

    class MainScreen(BoxLayout):
        server_button = StringProperty("Start fastAPI server")
        is_server_up = BooleanProperty(False)

        switch_value = StringProperty("N/A")
        slider_value = StringProperty("N/A")
        rotary_knob_value = StringProperty("N/A")
        select_value = StringProperty("N/A")

        ui_conn, server_conn = multiprocessing.Pipe()

        def on_server_button_click(self):
            print("clicked")
            if (self.is_server_up):
                stop_server()
                self.is_server_up = False
                self.server_button = "Start fastAPI server"
            else:
                self.start_update()
                self.is_server_up = True
                self.server_button = "Stop fastAPI server"
                start_server(self.server_conn)


        def start_update(self):
            Clock.schedule_interval(self.update_ui, 1)

        def update_ui(self, dt):
            if self.ui_conn.poll():
                control: ControlData = self.ui_conn.recv()
                if control.switch:
                    self.switch_value = 'ON'
                else:
                    self.switch_value = 'OFF'
                self.slider_value = str(control.slider)
                self.rotary_knob_value = str(control.rotary_knob)
                self.select_value = str(control.select)


    GDevice().run()