## Elasticsearch

### Install Elasticsearch on OS X

This installs Elasticsearch using [http://brew.sh/](Homebrew) (1.7.3 is the latest in Homebrew at the moment).

    brew install elasticsearch
    launchctl unload ~/Library/LaunchAgents/homebrew.mxcl.elasticsearch.plist


Elasticsearch is now installed, but won't start on boot. It has to be started manually.

### Elasticsearch Configuration for Development

The configuration file is at `/usr/local/opt/elasticsearch/config/elasticsearch.yml`. Make sure it only contains:


    cluster.name: elasticsearch
    path.data: /usr/local/var/elasticsearch/
    path.logs: /usr/local/var/log/elasticsearch/
    path.plugins: /usr/local/var/lib/elasticsearch/plugins
    network.bind_host: 127.0.0.1
    network.host: 127.0.0.1

### Adding indexes

Add the `tweet-vault` index:

```
curl -XPUT http://127.0.0.1:9200/tweet-vault/
```

### Running Elasticsearch

```
elasticsearch --config=/usr/local/etc/elasticsearch/elasticsearch.yml
```
