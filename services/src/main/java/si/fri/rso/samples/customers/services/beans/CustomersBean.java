package si.fri.rso.samples.customers.services.beans;

import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import si.fri.rso.samples.customers.models.dtos.Order;
import si.fri.rso.samples.customers.models.entities.Customer;
import si.fri.rso.samples.customers.services.configuration.AppProperties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


@RequestScoped
public class CustomersBean {

    private Logger log = Logger.getLogger(CustomersBean.class.getName());

    @Inject
    private EntityManager em;

    @Inject
    private AppProperties appProperties;

    @Inject
    private CustomersBean customersBean;

    private Client httpClient;

    @Inject
    @DiscoverService("rso-orders")
    private Optional<String> baseUrl;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
//        baseUrl = "http://localhost:8081"; // only for demonstration
    }


    public List<Customer> getCustomers() {

        TypedQuery<Customer> query = em.createNamedQuery("Customer.getAll", Customer.class);

        return query.getResultList();

    }

    public List<Customer> getCustomersFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, Customer.class, queryParameters);
    }

    public Customer getCustomer(Integer customerId) {

        Customer customer = em.find(Customer.class, customerId);

        if (customer == null) {
            throw new NotFoundException();
        }

        List<Order> orders = customersBean.getOrders(customerId);
        customer.setOrders(orders);

        return customer;
    }

    public Customer createCustomer(Customer customer) {

        try {
            beginTx();
            em.persist(customer);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return customer;
    }

    public Customer putCustomer(String customerId, Customer customer) {

        Customer c = em.find(Customer.class, customerId);

        if (c == null) {
            return null;
        }

        try {
            beginTx();
            customer.setId(c.getId());
            customer = em.merge(customer);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return customer;
    }

    public boolean deleteCustomer(String customerId) {

        Customer customer = em.find(Customer.class, customerId);

        if (customer != null) {
            try {
                beginTx();
                em.remove(customer);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else
            return false;

        return true;
    }

    public List<Order> getOrders(Integer customerId) {

        if (appProperties.isExternalServicesEnabled() && baseUrl.isPresent()) {
            try {
                return httpClient
                        .target(baseUrl.get() + "/v1/orders?where=customerId:EQ:" + customerId)
                        .request().get(new GenericType<List<Order>>() {
                        });
            } catch (WebApplicationException | ProcessingException e) {
                log.severe(e.getMessage());
                throw new InternalServerErrorException(e);
            }
        }

        return null;

    }

    private void beginTx() {
        if (!em.getTransaction().isActive())
            em.getTransaction().begin();
    }

    private void commitTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().commit();
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().rollback();
    }

    public void loadOrder(Integer n) {


    }
}
