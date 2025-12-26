import requests
import os
import json
from dotenv import load_dotenv

load_dotenv()

HA_URL = os.getenv("HA_URL")
HA_TOKEN = os.getenv("HA_TOKEN")

def get_entities():
    url = f"{HA_URL}/api/states"
    headers = {
        "Authorization": f"Bearer {HA_TOKEN}",
        "content-type": "application/json",
    }
    
    try:
        response = requests.get(url, headers=headers)
        response.raise_for_status()
        return response.json()
    except Exception as e:
        print(f"Error fetching entities: {e}")
        return None

def filter_entities(entities, domains):
    if not entities:
        return []
    return [e for e in entities if any(e['entity_id'].startswith(d) for d in domains)]

if __name__ == "__main__":
    if not HA_TOKEN or HA_TOKEN == "your_long_lived_access_token_here":
        print("Please set your HA_TOKEN in the .env file.")
    else:
        print(f"Fetching entities from {HA_URL}...")
        entities = get_entities()
        if entities:
            domains = ["light.", "switch.", "binary_sensor."]
            filtered = filter_entities(entities, domains)
            print(f"Found {len(filtered)} matching entities.")
            for e in filtered[:10]: # Show first 10
                print(f"- {e['entity_id']}: {e['state']}")
            if len(filtered) > 10:
                print(f"... and {len(filtered) - 10} more.")
