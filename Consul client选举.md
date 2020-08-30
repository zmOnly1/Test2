# Consul client选举

## create session

```shell
curl  -X PUT -d '{
    "Name": "test1",
	"LockDelay": "10s",
    "Behavior": "release",
    "TTL": "10s"
}' http://localhost:8500/v1/session/create

curl  -X PUT -d '{
    "Name": "test1",
	"LockDelay": "100s",
    "Behavior": "release",
    "Checks": ["service:springDemo-8080"]
}' http://localhost:8500/v1/session/create

curl  -X PUT -d '{
    "Name": "test1",
	"LockDelay": "10s",
    "Behavior": "release",
    "TTL": "20s",
    "Checks": ["service:springDemo-8080"]
}' http://localhost:8500/v1/session/create

8a3bda11-bf72-ec79-f42e-244e89801e8c
```

## delete session

```shell
curl  -X PUT http://localhost:8500/v1/session/destroy/:uuid
```

## read session

```shell
curl  -X GET http://localhost:8500/v1/session/info/8335c961-f6db-d781-e9d3-ad8dc53e8c8a

#blocking queries
```

## list session

```shell
curl  -X GET http://localhost:8500/v1/session/list

#blocking queries
```

## renew session

```shell
curl  -X PUT http://localhost:8500/v1/session/renew/:uuid
```

## list checks

```shell
curl http://localhost:8500/v1/agent/checks
--data-urlencode 'filter=ServiceID == springDemo'

```

## read key

```shell
#blocking query
http://localhost:8500/v1/kv/program/leader?wait=30s&index=2242

curl -X GET http://localhost:8500/v1/kv/test2
```

## update key

```shell
curl -X PUT -d '{"a":"b"}' http://localhost:8500/v1/kv/test2?acquire=36a5b4d2-b389-c846-20fa-a211554da070
```



## Discover the leader

### 1.retrieve the key

```shell
 curl -X GET http://localhost:8500/v1/kv/lead
[
  {
    "Session": "4ca8e74b-6350-7587-addf-a18084928f3c",
    "Value": "Ym9keQ==",
    "Flags": 0,
    "Key": "dbservice",
    "LockIndex": 1,
    "ModifyIndex": 29,
    "CreateIndex": 29
  }
]
```

### 2.retrieve the session

```shell
curl -X GET http://localhost:8500/v1/session/info/4ca8e74b-6350-7587-addf-a18084928f3c

[
  {
    "LockDelay": 1.5e10,
    "Checks": ["serfHealth"],
    "Node": "consul-primary-bjsiobmvdij6-node-lhe5ihreel7y",
    "Name": "dbservice",
    "ID": "4ca8e74b-6350-7587-addf-a18084928f3c",
    "CreateIndex": 28
  }
]
```



1. retrieve key
   1. session==null
      1. create session
      2. bind key with session
         1. value == self, bind directly
            1. sucess, Same leader
         2. value != self, delay bind
            1. sucess, Change leader
      3. new leader born if bind sucess, 
      4. bind fail, TTL session wait for timeout
   2. session!=null and value!=slef
      1. jump to watch
   3. session!=null and value==self
      1. get session id
      2. renew session, become leader, Same Leader
2. watching key change
   1. session == null
      1. jump to step 1
   2. session != null
      1. keep watching
      2. renew ression if leader, blocking wait time < ttl