package si.fri.rso.samples.customers.api.v1.resources;

import com.kumuluz.ee.logs.cdi.Log;
import si.fri.rso.samples.customers.models.entities.Customer;
import si.fri.rso.samples.customers.services.beans.CustomersBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
@Log
@ApplicationScoped
@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomersResource {

    @Inject
    private CustomersBean customersBean;

    @Context
    protected UriInfo uriInfo;

    @GET
    public Response getCustomers() {

        List<Customer> customers = customersBean.getCustomers();

        return Response.ok(customers).build();
    }
    @GET
    @Path("info")
    @Produces("application/json")
    public Response info() {

        JSONObject json = new JSONObject();

        JSONArray members = new JSONArray();
        members.put("as7849");
        members.put("ao2282");
        members.put("bj9914");

        JSONArray microservices = new JSONArray();
        microservices.put("http://169.51.20.134:31568/v1/customers");
        microservices.put("http://169.51.20.134:32564/v1/orders");
        microservices.put("http://169.51.20.63:31567/v1/products");

        JSONArray github = new JSONArray();
        github.put("https://github.com/cloud-computing-project/customers");
        github.put("https://github.com/cloud-computing-project/orders");
        github.put("https://github.com/cloud-computing-project/products");

        JSONArray travis = new JSONArray();
        travis.put("https://travis-ci.org/cloud-computing-project/customers");
        travis.put("https://travis-ci.org/cloud-computing-project/orders");
        travis.put("https://travis-ci.org/cloud-computing-project/products");

        JSONArray dockerhub = new JSONArray();
        dockerhub.put("https://hub.docker.com/r/amela/customers/");
        dockerhub.put("https://hub.docker.com/r/ejmric/orders/");
        dockerhub.put("https://hub.docker.com/r/bozen/products/");

        json.put("members", members);
        json.put("project_description", "Our project includes an application for shopping online(on the example of e-bay).");
        json.put("microservices", microservices);
        json.put("github", github);
        json.put("travis", travis);
        json.put("dockerhub", dockerhub);

        return Response.ok(json.toString()).build();
    }
    @GET
    @Path("/filtered")
    public Response getCustomersFiltered() {

        List<Customer> customers;

        customers = customersBean.getCustomersFilter(uriInfo);

        return Response.status(Response.Status.OK).entity(customers).build();
    }

    @GET
    @Path("/{customerId}")
    public Response getCustomer(@PathParam("customerId") Integer customerId) {

        Customer customer = customersBean.getCustomer(customerId);

        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(customer).build();
    }

    @POST
    public Response createCustomer(Customer customer) {

        if ((customer.getFirstName() == null || customer.getFirstName().isEmpty()) || (customer.getLastName() == null
                || customer.getLastName().isEmpty())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            customer = customersBean.createCustomer(customer);
        }

        if (customer.getId() != null) {
            return Response.status(Response.Status.CREATED).entity(customer).build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity(customer).build();
        }
    }

    @PUT
    @Path("{customerId}")
    public Response putZavarovanec(@PathParam("customerId") String customerId, Customer customer) {

        customer = customersBean.putCustomer(customerId, customer);

        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            if (customer.getId() != null)
                return Response.status(Response.Status.OK).entity(customer).build();
            else
                return Response.status(Response.Status.NOT_MODIFIED).build();
        }
    }

    @DELETE
    @Path("{customerId}")
    public Response deleteCustomer(@PathParam("customerId") String customerId) {

        boolean deleted = customersBean.deleteCustomer(customerId);

        if (deleted) {
            return Response.status(Response.Status.GONE).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
