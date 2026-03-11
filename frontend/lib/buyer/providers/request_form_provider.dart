import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../core/models/request_form_state.dart';
import '../../core/models/time_slot.dart';

class RequestFormNotifier extends StateNotifier<RequestFormState> {
  RequestFormNotifier() : super(const RequestFormState());

  // Step 1
  void togglePurposeTag(String tag) {
    final tags = [...state.purposeTags];
    tags.contains(tag) ? tags.remove(tag) : tags.add(tag);
    state = state.copyWith(purposeTags: tags);
  }

  void toggleRelationTag(String tag) {
    final tags = [...state.relationTags];
    tags.contains(tag) ? tags.remove(tag) : tags.add(tag);
    state = state.copyWith(relationTags: tags);
  }

  void toggleMoodTag(String tag) {
    final tags = [...state.moodTags];
    tags.contains(tag) ? tags.remove(tag) : tags.add(tag);
    state = state.copyWith(moodTags: tags);
  }

  // Step 2
  void setBudgetTier(String tier) {
    state = state.copyWith(budgetTier: tier);
  }

  // Step 3
  void setFulfillmentType(String type) {
    state = state.copyWith(
      fulfillmentType: type,
      requestedTimeSlots: [],
    );
  }

  void setPlace(String address, double lat, double lng) {
    state = state.copyWith(
      placeAddressText: address,
      placeLat: lat,
      placeLng: lng,
    );
  }

  void setFulfillmentDate(String date) {
    state = state.copyWith(fulfillmentDate: date);
  }

  // Step 4
  void toggleTimeSlot(TimeSlot slot) {
    final slots = [...state.requestedTimeSlots];
    final idx = slots.indexWhere(
      (s) => s.kind == slot.kind && s.value == slot.value,
    );
    idx >= 0 ? slots.removeAt(idx) : slots.add(slot);
    state = state.copyWith(requestedTimeSlots: slots);
  }

  void reset() {
    state = const RequestFormState();
  }
}

final requestFormProvider =
    StateNotifierProvider<RequestFormNotifier, RequestFormState>(
  (ref) => RequestFormNotifier(),
);
