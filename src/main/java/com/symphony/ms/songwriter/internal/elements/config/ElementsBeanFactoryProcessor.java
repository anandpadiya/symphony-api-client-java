package com.symphony.ms.songwriter.internal.elements.config;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.stereotype.Component;
import com.symphony.ms.songwriter.internal.elements.ElementsHandler;
import com.symphony.ms.songwriter.internal.scan.BaseBeanFactoryPostProcessor;

/**
 * Automatically scans for {@link ElementsHandler}, instantiates them, injects
 * all dependencies and registers to Spring bean registry.
 *
 * @author Marcus Secato
 *
 */
@Component
public class ElementsBeanFactoryProcessor extends BaseBeanFactoryPostProcessor {
  private static final Logger LOGGER = LoggerFactory.getLogger(ElementsBeanFactoryProcessor.class);

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    final BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) beanFactory;
    Set<BeanDefinition> beanDefinitionSet = scanComponents(ElementsHandler.class);
    LOGGER.info("Scanning for elements handlers found {} beans", beanDefinitionSet.size());

    for (BeanDefinition beanDefinition : beanDefinitionSet) {
      BeanDefinition eventHandler = BeanDefinitionBuilder
          .rootBeanDefinition(beanDefinition.getBeanClassName())
          .setInitMethodName("register")
          .addPropertyReference("commandDispatcher", "commandDispatcherImpl")
          .addPropertyReference("commandFilter", "commandFilterImpl")
          .addPropertyReference("messageService", "messageServiceImpl")
          .addPropertyReference("featureManager", "featureManager")
          .addPropertyReference("symphonyService", "symphonyServiceImpl")
          .addPropertyReference("eventDispatcher", "eventDispatcherImpl")
          .getBeanDefinition();

      beanDefinitionRegistry.registerBeanDefinition(
          beanDefinition.getBeanClassName(), eventHandler);
    }
  }

}
