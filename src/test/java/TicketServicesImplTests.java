import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.TicketService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidAccountNumberException;
import uk.gov.dwp.uc.pairtest.exception.TicketLimitException;
import uk.gov.dwp.uc.pairtest.exception.UnaccompaniedChildOrInfantException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.*;

public class TicketServicesImplTests {

    private static TicketTypeRequest singleAdultRequest, singleChildRequest, singleInfantRequest, twoAdultsRequest, bigRequest, tooBigRequest;
    private static Long validAccountNumber, invalidAccountNumber;
    private TicketService ticketServiceInstance;
    private SeatReservationService seatReservationServiceInstance;
    private TicketPaymentService ticketPaymentServiceInstance;

    @BeforeClass
    public static void setup() {
        validAccountNumber = 1L;
        invalidAccountNumber = -1L;
        singleAdultRequest = new TicketTypeRequest(ADULT, 1);
        singleChildRequest = new TicketTypeRequest(CHILD, 1);
        singleInfantRequest = new TicketTypeRequest(INFANT, 1);
        twoAdultsRequest = new TicketTypeRequest(ADULT, 2);
        bigRequest = new TicketTypeRequest(ADULT, 20);
        tooBigRequest = new TicketTypeRequest(ADULT, 21);
    }

    @Before
    public void init() {
        seatReservationServiceInstance = mock(SeatReservationService.class);
        ticketPaymentServiceInstance = mock(TicketPaymentService.class);
        ticketServiceInstance = new TicketServiceImpl(seatReservationServiceInstance, ticketPaymentServiceInstance);
    }

    @Test(expected = InvalidAccountNumberException.class)
    public void testPurchaseTicketsInvalidAccountNumber() {
        ticketServiceInstance.purchaseTickets(invalidAccountNumber, singleAdultRequest);
    }

    @Test(expected = UnaccompaniedChildOrInfantException.class)
    public void testPurchaseTicketsChildNoAdult() {
        ticketServiceInstance.purchaseTickets(validAccountNumber, singleChildRequest);
    }

    @Test(expected = UnaccompaniedChildOrInfantException.class)
    public void testPurchaseTicketsInfantNoAdult() {
        ticketServiceInstance.purchaseTickets(validAccountNumber, singleInfantRequest);
    }

    @Test(expected = UnaccompaniedChildOrInfantException.class)
    public void testPurchaseTicketsInfantAndChildNoAdult() {
        ticketServiceInstance.purchaseTickets(validAccountNumber, singleInfantRequest, singleChildRequest);
    }

    @Test(expected = TicketLimitException.class)
    public void testPurchaseTicketsOver20Tickets() {
        ticketServiceInstance.purchaseTickets(validAccountNumber, tooBigRequest);
    }

    @Test
    public void testPurchaseTicketsOneAdult() {
        ticketServiceInstance.purchaseTickets(validAccountNumber, singleAdultRequest);
        verify(seatReservationServiceInstance).reserveSeat(validAccountNumber, 1);
        verify(ticketPaymentServiceInstance).makePayment(validAccountNumber, 20);
    }

    @Test
    public void testPurchaseTicketsOneAdultOneInfant() {
        ticketServiceInstance.purchaseTickets(validAccountNumber, singleAdultRequest, singleInfantRequest);
        verify(seatReservationServiceInstance).reserveSeat(validAccountNumber, 1);
        verify(ticketPaymentServiceInstance).makePayment(validAccountNumber, 20);
    }

    @Test
    public void testPurchaseTicketsOneAdultOneChildOneInfant() {
        ticketServiceInstance.purchaseTickets(validAccountNumber, singleAdultRequest, singleChildRequest, singleInfantRequest);
        verify(seatReservationServiceInstance).reserveSeat(validAccountNumber, 2);
        verify(ticketPaymentServiceInstance).makePayment(validAccountNumber, 30);
    }

    @Test
    public void testPurchaseTicketsMultipleAdultRequests(){
        ticketServiceInstance.purchaseTickets(validAccountNumber, singleAdultRequest, twoAdultsRequest);
        verify(seatReservationServiceInstance).reserveSeat(validAccountNumber, 3);
        verify(ticketPaymentServiceInstance).makePayment(validAccountNumber, 60);
    }

    @Test
    public void testPurchaseTicketsTwentyAdults() {
        ticketServiceInstance.purchaseTickets(validAccountNumber, bigRequest);
        verify(seatReservationServiceInstance).reserveSeat(validAccountNumber, 20);
        verify(ticketPaymentServiceInstance).makePayment(validAccountNumber, 400);
    }
}
