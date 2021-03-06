package io.dropwizard.discovery.manage;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nonnull;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.zookeeper.data.Stat;
import io.dropwizard.lifecycle.Managed;

public class CuratorManager implements Managed {

    private final CuratorFramework framework;

    /**
     * Constructor
     * 
     * @param framework
     *            {@link CuratorFramework}
     */
    public CuratorManager(@Nonnull final CuratorFramework framework) {
        this.framework = checkNotNull(framework);
        // start framework directly to allow other bundles to interact with
        // zookeeper during their run() method.
        if (this.framework.getState() != CuratorFrameworkState.STARTED) {
            this.framework.start();
        }
    }

    @Override
    public void start() throws Exception {
        framework.blockUntilConnected();
        final Stat stat = framework.checkExists().forPath("/");
        if (stat == null) {
            // ensure that the root path is available
            framework.create().creatingParentsIfNeeded().forPath("/");
        }
    }

    @Override
    public void stop() throws Exception {
        framework.close();
    }
}
