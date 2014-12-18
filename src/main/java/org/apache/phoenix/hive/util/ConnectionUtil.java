/*
 * Copyright 2010 The Apache Software Foundation
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 *distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you maynot use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicablelaw or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.phoenix.hive.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.phoenix.util.PhoenixRuntime;
import org.apache.phoenix.util.QueryUtil;
import org.apache.phoenix.hive.PhoenixOutputFormat;
import org.apache.phoenix.hive.util.ConfigurationUtil;

import com.google.common.base.Preconditions;

/**
 * Describe your class here.
 *
 * @since 138
 */
public final class ConnectionUtil {
    private static final Log LOG = LogFactory.getLog(ConnectionUtil.class);
    
    /**
     * Returns the {#link Connection} from Configuration
     * @param configuration
     * @return
     * @throws SQLException
     */
    public static Connection getConnection(final Configuration configuration) throws SQLException {
        LOG.info("Connection getConnection...");
        Preconditions.checkNotNull(configuration);
        final Properties props = new Properties();
        String quorum = configuration.get(ConfigurationUtil.ZOOKEEPER_QUORUM!=null?ConfigurationUtil.ZOOKEEPER_QUORUM:configuration.get(HConstants.ZOOKEEPER_QUORUM));
        String znode = configuration.get(ConfigurationUtil.ZOOKEEPER_PARENT!=null?ConfigurationUtil.ZOOKEEPER_PARENT:configuration.get(HConstants.ZOOKEEPER_ZNODE_PARENT));
        String port = configuration.get(ConfigurationUtil.ZOOKEEPER_PORT!=null?ConfigurationUtil.ZOOKEEPER_PORT:configuration.get(HConstants.ZOOKEEPER_CLIENT_PORT));
        if (!znode.startsWith("/")) {
        	znode = "/" + znode;
        }
        
        try {
            Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
            LOG.info("Connection info: "+PhoenixRuntime.JDBC_PROTOCOL
                            + PhoenixRuntime.JDBC_PROTOCOL_SEPARATOR + quorum
                            + PhoenixRuntime.JDBC_PROTOCOL_SEPARATOR + port
                            + PhoenixRuntime.JDBC_PROTOCOL_SEPARATOR + znode);
            
            final Connection conn =
                    DriverManager.getConnection(PhoenixRuntime.JDBC_PROTOCOL
                            + PhoenixRuntime.JDBC_PROTOCOL_SEPARATOR + quorum
                            + PhoenixRuntime.JDBC_PROTOCOL_SEPARATOR + port
                            + PhoenixRuntime.JDBC_PROTOCOL_SEPARATOR + znode);
            String autocommit = configuration.get(ConfigurationUtil.AUTOCOMMIT);
            if(autocommit!=null && autocommit.equalsIgnoreCase("true")){
                conn.setAutoCommit(true);
            }else{
                conn.setAutoCommit(false);
            }
            return conn;
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }      
       return null; 
    }
    
    /**
     * Returns the {#link Connection} from Configuration
     * @param configuration
     * @return
     * @throws SQLException
     */
    
    public static Connection getConnection(final Table tbl) throws SQLException {
        Preconditions.checkNotNull(tbl);
        Map<String, String> TblParams = tbl.getParameters();
        String quorum =
                TblParams.get(ConfigurationUtil.ZOOKEEPER_QUORUM) != null ? TblParams.get(
                    ConfigurationUtil.ZOOKEEPER_QUORUM).trim()
                        : ConfigurationUtil.ZOOKEEPER_QUORUM_DEFAULT;
        String port =
                TblParams.get(ConfigurationUtil.ZOOKEEPER_PORT) != null ? TblParams.get(
                    ConfigurationUtil.ZOOKEEPER_PORT).trim()
                        : ConfigurationUtil.ZOOKEEPER_PORT_DEFAULT;
        String znode =
                TblParams.get(ConfigurationUtil.ZOOKEEPER_PARENT) != null ? TblParams.get(
                    ConfigurationUtil.ZOOKEEPER_PARENT).trim()
                        : ConfigurationUtil.ZOOKEEPER_PARENT_DEFAULT;
        if (!znode.startsWith("/")) {
        	znode = "/" + znode;
        }
        try {
            Class.forName("org.apache.phoenix.jdbc.PhoenixDriver");
            final Connection conn =
                    DriverManager.getConnection((PhoenixRuntime.JDBC_PROTOCOL
                            + PhoenixRuntime.JDBC_PROTOCOL_SEPARATOR + quorum
                            + PhoenixRuntime.JDBC_PROTOCOL_SEPARATOR + port
                            + PhoenixRuntime.JDBC_PROTOCOL_SEPARATOR + znode));
            String autocommit = TblParams.get(ConfigurationUtil.AUTOCOMMIT);
            if(autocommit!=null && autocommit.equalsIgnoreCase("true")){
                conn.setAutoCommit(true);
            }else{
                conn.setAutoCommit(false);
            }
            
            return conn;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
       
        return null;
    }
    
    
    /**
     * Close the connection.
     * @param conn
     * @throws SQLException 
     */
    public static void closeConnection(final Connection conn) throws SQLException {
        if(conn != null) {
           conn.close();
        }
    }
}
