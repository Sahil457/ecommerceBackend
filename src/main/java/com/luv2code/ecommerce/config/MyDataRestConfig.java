package com.luv2code.ecommerce.config;

import com.luv2code.ecommerce.entity.Country;
import com.luv2code.ecommerce.entity.Product;
import com.luv2code.ecommerce.entity.ProductCategory;
import com.luv2code.ecommerce.entity.State;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Configuration
public class MyDataRestConfig  implements RepositoryRestConfigurer {

    private EntityManager entityManager;

    @Autowired
    public MyDataRestConfig(EntityManager theEntityManager)
    {
        entityManager=theEntityManager;
    }


    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        RepositoryRestConfigurer.super.configureRepositoryRestConfiguration(config, cors);

        HttpMethod[] theUnsupportedActions ={ HttpMethod.PUT,HttpMethod.POST,HttpMethod.DELETE};

        //disable HTTP methods for Product: PUT,POST,DELETE
        disableHttpMethods(Product.class,config, theUnsupportedActions);
        //disable HTTP methods for ProductCategory: PUT,POST,DELETE
        disableHttpMethods(ProductCategory.class,config, theUnsupportedActions);
        disableHttpMethods(Country.class,config, theUnsupportedActions);
        disableHttpMethods(State.class,config, theUnsupportedActions);

        //call an internal helper method to expose id
        exposeIds(config);
    }

    private void disableHttpMethods(Class theClass,RepositoryRestConfiguration config, HttpMethod[] theUnsupportedActions) {
        config.getExposureConfiguration()
                .forDomainType(theClass)
                .withItemExposure((metdata, httpMethods) -> httpMethods.disable(theUnsupportedActions))
                .withCollectionExposure((metdata, httpMethods) -> httpMethods.disable(theUnsupportedActions));
    }

    private void exposeIds(RepositoryRestConfiguration config) {
        //expose entity ids


        //get list  of  all entity classes from entity Manager
        Set<EntityType<?>> entities= entityManager.getMetamodel().getEntities();

        //create array of entity types
        List<Class> entityClasses=new ArrayList<>();

        //get entity types for the entities
        for(EntityType tempEntityType : entities)
        {
            entityClasses.add(tempEntityType.getJavaType());
        }
        //expose entity id for array of entity types
        Class[] domainTypes= entityClasses.toArray(new Class[0]);
        config.exposeIdsFor(domainTypes);
    }
}
