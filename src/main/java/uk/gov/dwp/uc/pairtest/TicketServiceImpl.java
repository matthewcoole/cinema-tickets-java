package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidAccountNumberException;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.exception.TicketLimitException;
import uk.gov.dwp.uc.pairtest.exception.UnaccompaniedChildOrInfantException;

import static java.util.Arrays.stream;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.ADULT;
import static uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest.Type.INFANT;

/**
 * Ticket service class for handling payment and seat reservation based on ticket requests.
 */
public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */

    private SeatReservationService seatReservationServiceInstance;
    private TicketPaymentService ticketPaymentServiceInstance;

    /**
     * Constructor for the ticket service.
     *
     * @param seatReservationServiceInstance The seat reservation service to be invoked to reserve seats.
     * @param ticketPaymentServiceInstance   The payment service to be used to process payments.
     */
    public TicketServiceImpl(SeatReservationService seatReservationServiceInstance, TicketPaymentService ticketPaymentServiceInstance) {
        this.seatReservationServiceInstance = seatReservationServiceInstance;
        this.ticketPaymentServiceInstance = ticketPaymentServiceInstance;
    }

    private void checkAccountNumber(Long accountId) throws InvalidPurchaseException {
        if (accountId <= 0L)
            throw new InvalidAccountNumberException();
    }

    private void checkNonAdultsAreAccompanied(TicketTypeRequest... ticketTypeRequests) {
        for (TicketTypeRequest req : ticketTypeRequests) {
            if (req.getTicketType().equals(ADULT))
                return;
        }
        throw new UnaccompaniedChildOrInfantException();
    }

    private void checkMaximumTicketLimit(TicketTypeRequest[] ticketTypeRequests) {
        int ticketCount = stream(ticketTypeRequests).mapToInt(TicketTypeRequest::getNoOfTickets).sum();
        if (ticketCount > 20)
            throw new TicketLimitException();
    }

    private int getNumberOfSeatsRequired(TicketTypeRequest req) {
        if (!req.getTicketType().equals(INFANT))
            return req.getNoOfTickets();
        return 0;
    }

    private void reserveSeats(Long accountId, TicketTypeRequest[] ticketTypeRequests) {
        int seatsRequired = stream(ticketTypeRequests).mapToInt(this::getNumberOfSeatsRequired).sum();
        seatReservationServiceInstance.reserveSeat(accountId, seatsRequired);
    }

    private int calculatePrice(TicketTypeRequest req) {
        return PricingList.getPrice(req.getTicketType()) * req.getNoOfTickets();
    }

    private void makePayment(Long accountId, TicketTypeRequest[] ticketTypeRequests) {
        int totalPrice = stream(ticketTypeRequests).mapToInt(this::calculatePrice).sum();
        ticketPaymentServiceInstance.makePayment(accountId, totalPrice);
    }

    /**
     * Checks business rules and criteria and then purchases tickets based on requests, reserves seats, and takes payment.
     *
     * @param accountId          Account ID of purchaser (should be >= 1).
     * @param ticketTypeRequests Ticket requests to purchase.
     * @throws InvalidPurchaseException
     */
    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        //guard clauses
        checkAccountNumber(accountId);
        checkNonAdultsAreAccompanied(ticketTypeRequests);
        checkMaximumTicketLimit(ticketTypeRequests);

        //process seat reservation and payment
        reserveSeats(accountId, ticketTypeRequests);
        makePayment(accountId, ticketTypeRequests);
    }
}
