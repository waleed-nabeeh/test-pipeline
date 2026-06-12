function fn() {

  let env = karate.env
  karate.log('Execution Environment : ', env);
  let gatlingExecution = java.lang.System.getProperty('karate.gatling')

  let config = {
    isGatling: gatlingExecution,
    ACCESS_TOKEN_ADMIN: gatlingExecution?karate.get('accessTokenAdmin'):java.lang.System.getProperty('ACCESS_TOKEN_ADMIN'),
    ACCESS_TOKEN_BUYER: java.lang.System.getProperty('ACCESS_TOKEN_BUYER'),
    ACCESS_TOKEN_USER: java.lang.System.getProperty('ACCESS_TOKEN_USER'),
    storeIdentifier: java.lang.System.getProperty('storeIdentifier')
  };
  if (env === 'local' || env.length === 0) {
      config.baseUrl= 'http://localhost:9101';
      config.checkoutUrl = 'http://localhost:9102';
      config.ordermanagementUrl = 'http://localhost:9108';
      config.customerUrl = 'http://localhost:9105';
  } else {
      config.baseUrl = `https://productcatalog.${env}.b2x.b2bx.mindcurv.io`;
      config.checkoutUrl = `https://checkout.${env}.b2x.b2bx.mindcurv.io`;
      config.ordermanagementUrl = `https://ordermanagement.${env}.b2x.b2bx.mindcurv.io`;
      config.customerUrl = `https://customer.${env}.b2x.b2bx.mindcurv.io`;

  }
  return config;
}