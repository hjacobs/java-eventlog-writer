#!/usr/bin/python
# -*- coding: utf-8 -*-

import cStringIO
import unittest
from eventlog import EventlogError, _init, log, register
from logging import StreamHandler
from re import split


class TestEventlog(unittest.TestCase):

    def test_register_invalid_id(self):
        with self.assertRaises(EventlogError):
            register('id', 'NAME')

    def test_register_invalid_name(self):
        with self.assertRaises(EventlogError):
            register(1, 'EVENT__NAME')
        with self.assertRaises(EventlogError):
            register(1, 'event_name')

    def test_register_invalid_event(self):
        with self.assertRaises(EventlogError):
            register(1, 'EVENT_NAME', 'not_camel_case')

    def test_register_id_twice(self):
        register(1, 'EVENT_NAME', 'valid', 'alsoValid')
        with self.assertRaises(EventlogError):
            register(1, 'EVENT_NAME', 'valid', 'alsoValid')

    def test_log_unregistered_id(self):
        with self.assertRaises(EventlogError):
            log(1, param='test')

    def test_log_format(self):
        e_id = 12345
        log_stream = cStringIO.StringIO()
        layout_stream = cStringIO.StringIO()
        log_handler = StreamHandler(log_stream)
        layout_handler = StreamHandler(layout_stream)
        _init(log_handler, layout_handler)

        register(e_id, 'EVENT_NAME', 'valid', 'alsoValid')
        log(e_id, also_valid='second', valid='first')

        layout = split('\s+', layout_stream.getvalue().strip())
        eventlog = split('\s+', log_stream.getvalue().strip())

        self.assertEqual('{0:x}'.format(e_id), layout[2], 'Should have hex id in layout')
        self.assertEqual('{0:x}'.format(e_id), eventlog[2], 'Should have hex id in eventlog')
        self.assertEquals('first', eventlog[3], 'Should maintain registered order (1)')
        self.assertEquals('second', eventlog[4], 'Should maintain registered order (2)')


if __name__ == '__main__':
    unittest.main()