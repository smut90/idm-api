package net.cake.idm.core.base;

import net.cake.idm.core.dao.UserTokenDao;
import net.cake.idm.core.dao.impl.UserTokenDaoJPAImpl;
import net.cake.idm.core.service.api.IdmApiService;
import net.cake.idm.core.service.api.UserTokenService;
import net.cake.idm.core.service.impl.IdmApiServiceWSClientImpl;
import net.cake.idm.core.service.impl.UserTokenServiceImpl;
import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import play.api.inject.Module;
import scala.collection.Seq;

/**
 * Binding, interface classes to relevant implementation for Dependency Injection purposes
 * Binding is mentioned in application.conf under
 * play.modules.enabled += "net.cake.idm.core.base.BindModule"
 *
 * @author Supun Muthutantri
 * @date 18/02/2016
 */
public class BindModule extends Module {

    @Override
    public Seq<Binding<?>> bindings(Environment environment, Configuration configuration) {
        return seq(

                //Binding UserTokenService Interface with UserTokenServiceImpl class
                //When injecting UserTokenService interface, UserTokenServiceImpl will be used
                bind(UserTokenService.class).to(UserTokenServiceImpl.class),

                //When injecting UserTokenDao interface, UserTokenDaoJPAImpl will be used
                bind(UserTokenDao.class).to(UserTokenDaoJPAImpl.class),

                //When injecting IdmApiService interface, IdmApiServiceWSClientImpl will be used
                bind(IdmApiService.class).to(IdmApiServiceWSClientImpl.class)

        );
    }
}
