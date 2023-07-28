# Start your app
import os

from slack_bolt.adapter.socket_mode import SocketModeHandler

from slack_bot import get_app

if __name__ == "__main__":
    app = get_app()
    SocketModeHandler(app, os.environ["SLACK_APP_TOKEN"]).start()