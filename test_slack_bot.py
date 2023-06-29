import unittest

from slack_bot import filter_channels


class SlackBotTestCase(unittest.TestCase):
    def test_filters_channelIds(self):
        channel_names = ["channelA"]
        channel_response = [{"name": "channelA", "id": "channelAId", "is_member": True}, {"name": "channelB", "id": "channelBId", "is_member": False}]
        filtered_channel_ids = filter_channels(channel_response, channel_names)
        self.assertEqual(["channelAId"], filtered_channel_ids)


if __name__ == '__main__':
    unittest.main()
