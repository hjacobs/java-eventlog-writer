#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""
Generates a key pair for a zomcat instance
This key pair can be used for encrypting values in the Config Service
Requires python-keyczar package (available in Ubuntu repository)
"""

import os

from argparse import ArgumentParser
from keyczar import keyczart
from keyczar import keyinfo


def create_directory(target, key_type, environment, project):
    path = os.path.join(target, 'zomcat-keys', key_type, environment, project)
    if not os.path.isdir(path):
        os.makedirs(path)
    return path


def create_private_key(location, name):
    keyczart.Create(location, name, keyinfo.DECRYPT_AND_ENCRYPT, asymmetric='rsa')
    keyczart.AddKey(location, 'PRIMARY')
    print 'Created private key in %s' % location


def export_public_key(private_key_location, target):
    keyczart.PubKey(private_key_location, target)
    print 'Exported public key to %s' % target


def create_key_pair(environment, project, target):
    path = create_directory(target, 'private', environment, project)
    create_private_key(path, '%s-%s' % (project, environment))
    export_public_key(path, create_directory(target, 'public', environment, project))
    print 'Remember to set the owner and access rights of zomcat-keys correctly!'


if __name__ == '__main__':
    parser = ArgumentParser(description='Generates a keypair for a zomcat instance, as used by the Config Service')
    parser.add_argument('environment', help='Environment of the project, e.g. release-staging')
    parser.add_argument('project', help='Project name according to deployctl, e.g. catalog-service')
    parser.add_argument('target', help='Target directory. Should be the app directory, e.g. /data/zalando/app/p0120/')
    args = parser.parse_args()
    create_key_pair(args.environment, args.project, args.target)
