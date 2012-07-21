package org.quartz.management;

import org.quartz.core.QuartzScheduler;
import org.quartz.management.service.SamplerRepositoryService;
import org.terracotta.management.ServiceLocator;
import org.terracotta.management.embedded.StandaloneServer;

/**
 * @author Anthony Dahanne
 */
public final class ManagementServerImpl implements ManagementServer {

    private final StandaloneServer standaloneServer;

    private final SamplerRepositoryService samplerRepoSvc;

    public ManagementServerImpl(ManagementRESTServiceConfiguration configuration) {
        standaloneServer = new StandaloneServer();
        setupContainer(configuration);
        loadEmbeddedAgentServiceLocator();
        SamplerRepositoryService.Locator locator = EmbeddedQuartzServiceLocator.locator();
        this.samplerRepoSvc = locator.locateSamplerRepositoryService();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        try {
            standaloneServer.start();
        } catch (Exception e) {
            throw new RuntimeException("error starting management server", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        try {
            standaloneServer.stop();
        } catch (Exception e) {
            throw new RuntimeException("error stopping management server", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(QuartzScheduler managedResource) {
        samplerRepoSvc.register(managedResource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregister(QuartzScheduler managedResource) {
        samplerRepoSvc.unregister(managedResource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasRegistered() {
        return true;
        // return samplerRepoSvc.hasRegistered();
    }

    private void setupContainer(ManagementRESTServiceConfiguration configuration) {
        standaloneServer.setBasePackage("org.quartz.management.resource.services;org.quartz.management.jaxrs");
        standaloneServer.setHost(configuration.getHost());
        standaloneServer.setPort(configuration.getPort());
    }

    private void loadEmbeddedAgentServiceLocator() {
        DfltSamplerRepositoryService samplerRepoSvc = new DfltSamplerRepositoryService();
        ServiceLocator.load(new EmbeddedQuartzServiceLocator(null, samplerRepoSvc, samplerRepoSvc));
    }
}