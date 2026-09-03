package com.eventbooking.user.serviceimpl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventbooking.user.dto.EventCenterResponse;
import com.eventbooking.user.dto.RegisterEventCenterRequest;
import com.eventbooking.user.entity.EventCenter;
import com.eventbooking.user.entity.EventCenterStatus;
import com.eventbooking.user.entity.Role;
import com.eventbooking.user.entity.User;
import com.eventbooking.user.exception.ResourceAlreadyExistsException;
import com.eventbooking.user.exception.ResourceNotFoundException;
import com.eventbooking.user.repository.EventCenterRepository;
import com.eventbooking.user.repository.UserRepository;
import com.eventbooking.user.service.EventCenterService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EventCenterServiceImpl implements EventCenterService {

    private final UserRepository userRepository;
    private final EventCenterRepository eventCenterRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public EventCenterResponse registerEventCenter(
            RegisterEventCenterRequest request) 
{
        if (userRepository.existsByEmail(request.email())) 
        {
            throw new ResourceAlreadyExistsException(
                    "User already exists with email: " + request.email()
            );
        }

        String encodedPassword =
                passwordEncoder.encode(request.password());

        User user = User.builder()
                .name(request.ownerName())
                .email(request.email())
                .password(encodedPassword)
                .role(Role.EVENT_CENTER)
                .active(true)
                .build();

        User savedUser = userRepository.save(user);

        EventCenter eventCenter = EventCenter.builder()
                .user(savedUser)
                .centerName(request.centerName())
                .address(request.address())
                .city(request.city())
                .state(request.state())
                .country(request.country())
                .contactNumber(request.contactNumber())
                .description(request.description())
                .status(EventCenterStatus.PENDING)
                .build();

        EventCenter savedEventCenter =
                eventCenterRepository.save(eventCenter);
        return mapToResponse(savedEventCenter);
    }

    @Override
    @Transactional(readOnly = true)
    public EventCenterResponse getEventCenterById(Long id) 
    {
        EventCenter eventCenter =
                eventCenterRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Event center not found with id: " + id
                                )
                        );
        return mapToResponse(eventCenter);
    }

    @Override
    @Transactional(readOnly = true)
    public EventCenterResponse getEventCenterByUserId(Long userId) 
    {
        EventCenter eventCenter =
                eventCenterRepository.findByUserId(userId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Event center not found for user: "
                                                + userId
                                )
                        );
        return mapToResponse(eventCenter);
    }

    private EventCenterResponse mapToResponse(
            EventCenter eventCenter) 
   {
        User user = eventCenter.getUser();

        return new EventCenterResponse(
                eventCenter.getId(),
                user.getId(),
                user.getName(),
                user.getEmail(),
                eventCenter.getCenterName(),
                eventCenter.getAddress(),
                eventCenter.getCity(),
                eventCenter.getState(),
                eventCenter.getCountry(),
                eventCenter.getContactNumber(),
                eventCenter.getDescription(),
                eventCenter.getStatus(),
                eventCenter.getRejectionReason()
        );
    }
}