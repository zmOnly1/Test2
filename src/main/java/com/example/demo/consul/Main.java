package com.example.demo.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.kv.model.GetValue;
import com.ecwid.consul.v1.kv.model.PutParams;
import com.ecwid.consul.v1.session.model.NewSession;
import com.ecwid.consul.v1.session.model.Session;
import org.apache.commons.lang3.StringUtils;

/**
 * consul leader 选举演示程序
 *
 * @author: bossma.cn
 */
public class Main {

    private static ConsulClient client = new ConsulClient();
    private static String sesssionId = "";
    private static String nodeId = "007";
    private static String electName = "program/leader";

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("starting");
        watch();
    }

    /**
     * 监控选举
     *
     * @param:
     * @return:
     * @author: bossma.cn
     */
    private static void watch() {

        System.out.println("start first leader election");

        // 上来就先选举一次，看看结果
        ElectResponse electResponse = elect();
        System.out.printf("elect result: %s, current manager: %s" + System.getProperty("line.separator"), electResponse.getElectResult(), electResponse.getLeaderId());

        long waitIndex = electResponse.modifyIndex++;
        int waitTime = 30;

        do {
            try {
                System.out.println("start leader watch query");

                // 阻塞查询
                GetValue kv = getKVValue(electName, waitTime, waitIndex);

                // kv被删除或者kv绑定的session不存在
                if (null == kv || StringUtils.isEmpty(kv.getSession())) {
                    System.out.println("leader missing, start election right away");
                    electResponse = elect();
                    waitIndex = electResponse.modifyIndex++;
                    System.out.printf("elect result: %s, current manager: %s" + System.getProperty("line.separator"), electResponse.getElectResult(), electResponse.getLeaderId());
                } else {
                    long kvModifyIndex = kv.getModifyIndex();
                    waitIndex = kvModifyIndex++;
                }
            } catch (Exception ex) {
                System.out.print("leader watch异常：" + ex.getMessage());

                try {
                    Thread.sleep(3000);
                } catch (Exception ex2) {
                    System.out.printf(ex2.getMessage());
                }
            }
        }
        while (true);
    }

    /**
     * 执行选举
     *
     * @param:
     * @return:
     * @author: bossma.cn
     */
    private static ElectResponse elect() {

        ElectResponse response = new ElectResponse();

        Boolean electResult = false;

        // 创建一个关联到当前节点的Session
        if (StringUtils.isNotEmpty(sesssionId)) {
            Session s = getSession(sesssionId);
            if (null == s) {
                // 这里session关联的健康检查只绑定了consul节点的健康检查
                // 实际使用的时候建议把当前应用程序的健康检查也绑定上，否则如果只是程序关掉了，session也不会失效
                sesssionId = createSession(10);
            }
        } else {
            sesssionId = createSession(10);
        }

        // 获取选举要锁定的Consul KV对象
        GetValue kv = getKVValue(electName);
        if (null == kv) {
            kv = new GetValue();
        }

        // 谁先捕获到KV，谁就是leader
        // 注意：如果程序关闭后很快启动，session关联的健康检查可能不会失败，所以session不会失效
        // 这时候可以把程序创建的sessionId保存起来，重启后首先尝试用上次的sessionId，
        electResult = acquireKV(electName, nodeId, sesssionId);

        // 无论参选成功与否，获取当前的Leader
        kv = getKVValue(electName);
        response.setElectResult(electResult);
        response.setLeaderId(kv.getDecodedValue());
        response.setModifyIndex(kv.getModifyIndex());
        return response;
    }

    /**
     * 创建Session
     *
     * @param: lockDealy session从kv释放后，kv再次绑定session的延迟时间
     * @return:
     * @author: bossma.cn
     */
    private static String createSession(int lockDelay) {
        NewSession session = new NewSession();
        session.setLockDelay(lockDelay);
        session.setTtl("10s");

        return client.sessionCreate(session, QueryParams.DEFAULT).getValue();
    }

    /**
     * 获取指定的session信息
     *
     * @param: sessionId
     * @return: Session对象
     * @author: bossma.cn
     */
    private static Session getSession(String sessionId) {
        return client.getSessionInfo(sessionId, QueryParams.DEFAULT).getValue();
    }


    /**
     * 使用Session捕获KV
     *
     * @param key
     * @param value
     * @param sessionId
     * @return
     * @author: bossma.cn
     */
    public static Boolean acquireKV(String key, String value, String sessionId) {
        PutParams putParams = new PutParams();
        putParams.setAcquireSession(sessionId);

        return client.setKVValue(key, value, putParams).getValue();
    }

    /**
     * 获取指定key对应的值
     *
     * @param: key
     * @return:
     * @author: bossma.cn
     */
    public static GetValue getKVValue(String key) {
        return client.getKVValue(key).getValue();
    }

    /**
     * block获取指定key对应的值
     *
     * @param: key, waitTime, waitIndex
     * @return:
     * @author: bossma.cn
     */
    public static GetValue getKVValue(String key, int waitTime, long waitIndex) {
        QueryParams paras = QueryParams.Builder.builder()
                .setWaitTime(waitTime)
                .setIndex(waitIndex)
                .build();
        return client.getKVValue(key, paras).getValue();
    }

    /**
     * leader选举结果
     *
     * @author: bossma.cn
     */
    private static class ElectResponse {

        private Boolean electResult = false;
        private long modifyIndex = 0;
        private String leaderId;

        public String getLeaderId() {
            return leaderId;
        }

        public void setLeaderId(String leaderId) {
            this.leaderId = leaderId;
        }

        public Boolean getElectResult() {
            return electResult;
        }

        public void setElectResult(Boolean electResult) {
            this.electResult = electResult;
        }

        public long getModifyIndex() {
            return modifyIndex;
        }

        public void setModifyIndex(long modifyIndex) {
            this.modifyIndex = modifyIndex;
        }
    }
}