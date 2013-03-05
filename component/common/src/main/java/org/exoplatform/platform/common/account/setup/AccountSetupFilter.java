package org.exoplatform.platform.common.account.setup;

import org.exoplatform.commons.info.ProductInformations;
import org.exoplatform.commons.utils.PropertyManager;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.web.filter.Filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author <a href="fbradai@exoplatform.com">Fbradai</a>
 * @date 3/4/13
 */
public class AccountSetupFilter implements Filter {

    private static final Log LOG = ExoLogger.getLogger(AccountSetupFilter.class);
    private boolean isFirstAccess = true;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        String edition = getPlatformEdition();
        if(edition!=null && edition.equals("community")){
            ProductInformations productInformations = (ProductInformations) PortalContainer.getInstance().getComponentInstanceOfType(ProductInformations.class);
            boolean isDevMod = PropertyManager.isDevelopping();
            if((productInformations.isFirstRun())&&(isFirstAccess)&&(!isDevMod)){
                HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                httpServletResponse.sendRedirect("/platform-extension/jsp/welcome-screens/accountSetup.jsp");
                isFirstAccess = false;
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private String getPlatformEdition() {
        try {
            Class<?> c = Class.forName("org.exoplatform.platform.edition.PlatformEdition");
            Method getEditionMethod = c.getMethod("getEdition");
            String platformEdition = (String) getEditionMethod.invoke(null);
            return platformEdition;
        } catch (Exception e) {
            LOG.error("An error occured while getting the platform edition information.", e);
        }
        return null;
    }
}
