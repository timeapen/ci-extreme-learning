#!/bin/bash

if [ -z "${ssl_cert_dir}" ]; then
  ssl_cert_dir=`dirname $0`/ssl-certificates
fi

echo "installing ssl certificates from ${ssl_cert_dir}"
cp ${ssl_cert_dir}/* /etc/pki/ca-trust/source/anchors/
update-ca-trust extract
