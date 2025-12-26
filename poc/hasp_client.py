import os
import json
import requests
import paho.mqtt.client as mqtt
from dotenv import load_dotenv

load_dotenv()

MQTT_HOST = os.getenv("MQTT_HOST")
MQTT_PORT = int(os.getenv("MQTT_PORT", 1883))
MQTT_USER = os.getenv("MQTT_USER")
MQTT_PASS = os.getenv("MQTT_PASS")
HASP_IP = os.getenv("HASP_IP")
HASP_NODENAME = os.getenv("HASP_NODENAME")

def push_config_mqtt(jsonl_content):
    client = mqtt.Client()
    client.username_pw_set(MQTT_USER, MQTT_PASS)
    
    topic = f"hasp/{HASP_NODENAME}/command/jsonl"
    
    try:
        client.connect(MQTT_HOST, MQTT_PORT, 60)
        client.publish(topic, jsonl_content)
        print(f"Pushed config to {topic}")
        client.disconnect()
    except Exception as e:
        print(f"Error pushing config via MQTT: {e}")

def get_screenshot():
    url = f"http://{HASP_IP}/screenshot.bmp"
    try:
        response = requests.get(url, timeout=5)
        response.raise_for_status()
        with open("poc/screenshot.bmp", "wb") as f:
            f.write(response.content)
        print(f"Screenshot saved to poc/screenshot.bmp")
    except Exception as e:
        print(f"Error fetching screenshot: {e}")

if __name__ == "__main__":
    import argparse
    parser = argparse.ArgumentParser()
    parser.add_argument("--push-config", action="store_true")
    parser.add_argument("--screenshot", action="store_true")
    args = parser.parse_args()
    
    if args.push_config:
        # Example JSONL: Create a simple button on page 1
        config = '{"page":1,"id":1,"obj":"btn","x":10,"y":10,"w":100,"h":50,"text":"POC Button"}\n'
        push_config_mqtt(config)
    
    if args.screenshot:
        get_screenshot()
