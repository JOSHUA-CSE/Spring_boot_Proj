package com.example.trainbookingsystem;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.trainbookingsystem.entity.Ticket;
import com.example.trainbookingsystem.entity.Train;
import com.example.trainbookingsystem.entity.User;
import com.example.trainbookingsystem.repository.TicketRepository;
import com.example.trainbookingsystem.repository.TrainRepository;
import com.example.trainbookingsystem.repository.UserRepository;
import com.example.trainbookingsystem.service.TicketService;
import com.example.trainbookingsystem.service.TrainService;
import com.example.trainbookingsystem.service.UserService;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;

    private User user;
    
    
    @Mock
    private TrainRepository trainRepository;

    @Mock
    private TrainService trainService;

    private Train train;
    
    
    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketService ticketService;

    private Ticket ticket;

    @BeforeEach
    void setUp() {
        ticket = new Ticket();
        ticket.setId(1L);
        user = new User();
        user.setId(10L);
        user.setName("Test User");

        train = new Train();
        train.setId(20L);
        train.setName("Test Train");

        ticket.setUser(user);
        ticket.setTrain(train);
        ticket.setBookingDate(LocalDateTime.now());
        ticket.setFinalPrice(500.0);
        
    }

    @Test
    void getAllTickets_ShouldReturnListOftickets() {
        List<Ticket> tickets = Arrays.asList(ticket);
        when(ticketRepository.findAll()).thenReturn(tickets);

        List<Ticket> result = ticketService.getAllTickets();

        assertEquals(1, result.size());
        assertEquals(ticket.getId(),result.get(0).getId());
        verify(ticketRepository, times(1)).findAll();
    }

    @Test
    void getTicketById_WhenTicketExists_ShouldReturnTickets() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        Optional<Ticket> result = ticketService.getTicketById(1L);

        assertTrue(result.isPresent());
        assertEquals(ticket.getId(), result.get().getId());
        verify(ticketRepository, times(1)).findById(1L);
    }

    @Test
    void getTicketById_WhenTicketDoesNotExist_ShouldReturnEmpty() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Ticket> result = ticketService.getTicketById(1L);

        assertFalse(result.isPresent());
        verify(ticketRepository, times(1)).findById(1L);
    }

    @Test
    void createTicket_ShouldSaveAndReturnTicket() {
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);
        when(userService.getUserById(user.getId())).thenReturn(Optional.of(user));
        when(trainService.getTrainById(train.getId())).thenReturn(Optional.of(train));

        Ticket result = ticketService.createTicket(user.getId(), train.getId());

        assertNotNull(result);
        assertEquals(ticket.getId(), result.getId());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void updateTicket_WhenTicketExists_ShouldUpdateAndReturnTicket() {
        Train t=new Train();
        t.setName("SF EXPRESS");
        t.setSource("MADURAI");
        t.setDestination("LADAKH");
        t.setBasePrice(7000);
        Ticket updatedTicket = new Ticket();
        updatedTicket.setTrain(t);
    

        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        Ticket result = ticketService.updateTicket(1L, updatedTicket);

        assertEquals(updatedTicket.getTrain(), result.getTrain());
        verify(ticketRepository, times(1)).findById(1L);
        verify(ticketRepository, times(1)).save(ticket);
    }

    @Test
    void updateTicket_WhenTicketDoesNotExist_ShouldThrowException() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> ticketService.updateTicket(1L, ticket));
        verify(ticketRepository, times(1)).findById(1L);
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    void deleteTicket_ShouldDeleteTicket() {
        doNothing().when(ticketRepository).deleteById(1L);

        ticketService.deleteTicket(1L);

        verify(ticketRepository, times(1)).deleteById(1L);
    }
}