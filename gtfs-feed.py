import os

import requests
from google.transit import gtfs_realtime_pb2

GTFS_KEY = 'f819d2d4c18a4294be17be50bfc1a21b'  # os.environ['TRAFIKLAB_GTFS_KEY']


def get_feed():
    feed = gtfs_realtime_pb2.FeedMessage()
    response = requests.get(f'https://opendata.samtrafiken.se/gtfs-rt/sl/TripUpdates.pb?key={GTFS_KEY}')
    feed.ParseFromString(response.content)
    print(feed.header)
    for entity in feed.entity:

            print(entity)


if __name__ == "__main__":
    get_feed()
