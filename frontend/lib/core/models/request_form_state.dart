import 'package:freezed_annotation/freezed_annotation.dart';

import 'time_slot.dart';

part 'request_form_state.freezed.dart';

@freezed
class RequestFormState with _$RequestFormState {
  const RequestFormState._();

  const factory RequestFormState({
    // Step 1
    @Default([]) List<String> purposeTags,
    @Default([]) List<String> relationTags,
    @Default([]) List<String> moodTags,
    // Step 2
    String? budgetTier,
    // Step 3
    String? fulfillmentType,
    String? placeAddressText,
    double? placeLat,
    double? placeLng,
    String? fulfillmentDate,
    // Step 4
    @Default([]) List<TimeSlot> requestedTimeSlots,
  }) = _RequestFormState;

  bool get isStep1Valid =>
      purposeTags.isNotEmpty && relationTags.isNotEmpty;

  bool get isStep2Valid => budgetTier != null;

  bool get isStep3Valid =>
      fulfillmentType != null &&
      placeAddressText != null &&
      fulfillmentDate != null;

  bool get isStep4Valid => requestedTimeSlots.isNotEmpty;

  bool get isPickup => fulfillmentType == 'PICKUP';
}
