#!/usr/bin/env bash
set -x
echo "Executing postconfigure.sh"
echo CLIENT_SECRET=$CLIENT_SECRET
echo SSO_URL=$SSO_URL
$JBOSS_HOME/bin/jboss-cli.sh --file=$JBOSS_HOME/extensions/extensions.cli
