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
    @Produces(MediaType.APPLICATION_JSON)
    public Response info() {

        JsonObject json = Json.createObjectBuilder()
                .add("clani", Json.createArrayBuilder().add("jm1234"))
                .add("opis_projekta", "Nas projekt implementira aplikacijo za upravljanje naročil.")
                .add("mikrostoritve", Json.createArrayBuilder().add("http://35.204.91.158:8081/v1/orders"))
                .add("github", Json.createArrayBuilder().add("https://github.com/jmezna/rso-customers"))
                .add("travis", Json.createArrayBuilder().add("https://travis-ci.org/jmezna/rso-customers"))
                .add("dockerhub", Json.createArrayBuilder().add("https://hub.docker.com/r/jmezna/rso-customers"))
                .build();


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
