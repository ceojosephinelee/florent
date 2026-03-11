import 'package:freezed_annotation/freezed_annotation.dart';

part 'time_slot.freezed.dart';
part 'time_slot.g.dart';

@freezed
class TimeSlot with _$TimeSlot {
  const factory TimeSlot({
    required String kind,
    required String value,
  }) = _TimeSlot;

  factory TimeSlot.fromJson(Map<String, dynamic> json) =>
      _$TimeSlotFromJson(json);
}
