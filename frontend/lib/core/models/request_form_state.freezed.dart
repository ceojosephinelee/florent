// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'request_form_state.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
  'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#adding-getters-and-methods-to-our-models',
);

/// @nodoc
mixin _$RequestFormState {
  // Step 1
  List<String> get purposeTags => throw _privateConstructorUsedError;
  List<String> get relationTags => throw _privateConstructorUsedError;
  List<String> get moodTags => throw _privateConstructorUsedError; // Step 2
  String? get budgetTier => throw _privateConstructorUsedError; // Step 3
  String? get fulfillmentType => throw _privateConstructorUsedError;
  String? get placeAddressText => throw _privateConstructorUsedError;
  double? get placeLat => throw _privateConstructorUsedError;
  double? get placeLng => throw _privateConstructorUsedError;
  String? get fulfillmentDate => throw _privateConstructorUsedError; // Step 4
  List<TimeSlot> get requestedTimeSlots => throw _privateConstructorUsedError;

  /// Create a copy of RequestFormState
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $RequestFormStateCopyWith<RequestFormState> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $RequestFormStateCopyWith<$Res> {
  factory $RequestFormStateCopyWith(
    RequestFormState value,
    $Res Function(RequestFormState) then,
  ) = _$RequestFormStateCopyWithImpl<$Res, RequestFormState>;
  @useResult
  $Res call({
    List<String> purposeTags,
    List<String> relationTags,
    List<String> moodTags,
    String? budgetTier,
    String? fulfillmentType,
    String? placeAddressText,
    double? placeLat,
    double? placeLng,
    String? fulfillmentDate,
    List<TimeSlot> requestedTimeSlots,
  });
}

/// @nodoc
class _$RequestFormStateCopyWithImpl<$Res, $Val extends RequestFormState>
    implements $RequestFormStateCopyWith<$Res> {
  _$RequestFormStateCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of RequestFormState
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? purposeTags = null,
    Object? relationTags = null,
    Object? moodTags = null,
    Object? budgetTier = freezed,
    Object? fulfillmentType = freezed,
    Object? placeAddressText = freezed,
    Object? placeLat = freezed,
    Object? placeLng = freezed,
    Object? fulfillmentDate = freezed,
    Object? requestedTimeSlots = null,
  }) {
    return _then(
      _value.copyWith(
            purposeTags: null == purposeTags
                ? _value.purposeTags
                : purposeTags // ignore: cast_nullable_to_non_nullable
                      as List<String>,
            relationTags: null == relationTags
                ? _value.relationTags
                : relationTags // ignore: cast_nullable_to_non_nullable
                      as List<String>,
            moodTags: null == moodTags
                ? _value.moodTags
                : moodTags // ignore: cast_nullable_to_non_nullable
                      as List<String>,
            budgetTier: freezed == budgetTier
                ? _value.budgetTier
                : budgetTier // ignore: cast_nullable_to_non_nullable
                      as String?,
            fulfillmentType: freezed == fulfillmentType
                ? _value.fulfillmentType
                : fulfillmentType // ignore: cast_nullable_to_non_nullable
                      as String?,
            placeAddressText: freezed == placeAddressText
                ? _value.placeAddressText
                : placeAddressText // ignore: cast_nullable_to_non_nullable
                      as String?,
            placeLat: freezed == placeLat
                ? _value.placeLat
                : placeLat // ignore: cast_nullable_to_non_nullable
                      as double?,
            placeLng: freezed == placeLng
                ? _value.placeLng
                : placeLng // ignore: cast_nullable_to_non_nullable
                      as double?,
            fulfillmentDate: freezed == fulfillmentDate
                ? _value.fulfillmentDate
                : fulfillmentDate // ignore: cast_nullable_to_non_nullable
                      as String?,
            requestedTimeSlots: null == requestedTimeSlots
                ? _value.requestedTimeSlots
                : requestedTimeSlots // ignore: cast_nullable_to_non_nullable
                      as List<TimeSlot>,
          )
          as $Val,
    );
  }
}

/// @nodoc
abstract class _$$RequestFormStateImplCopyWith<$Res>
    implements $RequestFormStateCopyWith<$Res> {
  factory _$$RequestFormStateImplCopyWith(
    _$RequestFormStateImpl value,
    $Res Function(_$RequestFormStateImpl) then,
  ) = __$$RequestFormStateImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({
    List<String> purposeTags,
    List<String> relationTags,
    List<String> moodTags,
    String? budgetTier,
    String? fulfillmentType,
    String? placeAddressText,
    double? placeLat,
    double? placeLng,
    String? fulfillmentDate,
    List<TimeSlot> requestedTimeSlots,
  });
}

/// @nodoc
class __$$RequestFormStateImplCopyWithImpl<$Res>
    extends _$RequestFormStateCopyWithImpl<$Res, _$RequestFormStateImpl>
    implements _$$RequestFormStateImplCopyWith<$Res> {
  __$$RequestFormStateImplCopyWithImpl(
    _$RequestFormStateImpl _value,
    $Res Function(_$RequestFormStateImpl) _then,
  ) : super(_value, _then);

  /// Create a copy of RequestFormState
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? purposeTags = null,
    Object? relationTags = null,
    Object? moodTags = null,
    Object? budgetTier = freezed,
    Object? fulfillmentType = freezed,
    Object? placeAddressText = freezed,
    Object? placeLat = freezed,
    Object? placeLng = freezed,
    Object? fulfillmentDate = freezed,
    Object? requestedTimeSlots = null,
  }) {
    return _then(
      _$RequestFormStateImpl(
        purposeTags: null == purposeTags
            ? _value._purposeTags
            : purposeTags // ignore: cast_nullable_to_non_nullable
                  as List<String>,
        relationTags: null == relationTags
            ? _value._relationTags
            : relationTags // ignore: cast_nullable_to_non_nullable
                  as List<String>,
        moodTags: null == moodTags
            ? _value._moodTags
            : moodTags // ignore: cast_nullable_to_non_nullable
                  as List<String>,
        budgetTier: freezed == budgetTier
            ? _value.budgetTier
            : budgetTier // ignore: cast_nullable_to_non_nullable
                  as String?,
        fulfillmentType: freezed == fulfillmentType
            ? _value.fulfillmentType
            : fulfillmentType // ignore: cast_nullable_to_non_nullable
                  as String?,
        placeAddressText: freezed == placeAddressText
            ? _value.placeAddressText
            : placeAddressText // ignore: cast_nullable_to_non_nullable
                  as String?,
        placeLat: freezed == placeLat
            ? _value.placeLat
            : placeLat // ignore: cast_nullable_to_non_nullable
                  as double?,
        placeLng: freezed == placeLng
            ? _value.placeLng
            : placeLng // ignore: cast_nullable_to_non_nullable
                  as double?,
        fulfillmentDate: freezed == fulfillmentDate
            ? _value.fulfillmentDate
            : fulfillmentDate // ignore: cast_nullable_to_non_nullable
                  as String?,
        requestedTimeSlots: null == requestedTimeSlots
            ? _value._requestedTimeSlots
            : requestedTimeSlots // ignore: cast_nullable_to_non_nullable
                  as List<TimeSlot>,
      ),
    );
  }
}

/// @nodoc

class _$RequestFormStateImpl extends _RequestFormState {
  const _$RequestFormStateImpl({
    final List<String> purposeTags = const [],
    final List<String> relationTags = const [],
    final List<String> moodTags = const [],
    this.budgetTier,
    this.fulfillmentType,
    this.placeAddressText,
    this.placeLat,
    this.placeLng,
    this.fulfillmentDate,
    final List<TimeSlot> requestedTimeSlots = const [],
  }) : _purposeTags = purposeTags,
       _relationTags = relationTags,
       _moodTags = moodTags,
       _requestedTimeSlots = requestedTimeSlots,
       super._();

  // Step 1
  final List<String> _purposeTags;
  // Step 1
  @override
  @JsonKey()
  List<String> get purposeTags {
    if (_purposeTags is EqualUnmodifiableListView) return _purposeTags;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableListView(_purposeTags);
  }

  final List<String> _relationTags;
  @override
  @JsonKey()
  List<String> get relationTags {
    if (_relationTags is EqualUnmodifiableListView) return _relationTags;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableListView(_relationTags);
  }

  final List<String> _moodTags;
  @override
  @JsonKey()
  List<String> get moodTags {
    if (_moodTags is EqualUnmodifiableListView) return _moodTags;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableListView(_moodTags);
  }

  // Step 2
  @override
  final String? budgetTier;
  // Step 3
  @override
  final String? fulfillmentType;
  @override
  final String? placeAddressText;
  @override
  final double? placeLat;
  @override
  final double? placeLng;
  @override
  final String? fulfillmentDate;
  // Step 4
  final List<TimeSlot> _requestedTimeSlots;
  // Step 4
  @override
  @JsonKey()
  List<TimeSlot> get requestedTimeSlots {
    if (_requestedTimeSlots is EqualUnmodifiableListView)
      return _requestedTimeSlots;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableListView(_requestedTimeSlots);
  }

  @override
  String toString() {
    return 'RequestFormState(purposeTags: $purposeTags, relationTags: $relationTags, moodTags: $moodTags, budgetTier: $budgetTier, fulfillmentType: $fulfillmentType, placeAddressText: $placeAddressText, placeLat: $placeLat, placeLng: $placeLng, fulfillmentDate: $fulfillmentDate, requestedTimeSlots: $requestedTimeSlots)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$RequestFormStateImpl &&
            const DeepCollectionEquality().equals(
              other._purposeTags,
              _purposeTags,
            ) &&
            const DeepCollectionEquality().equals(
              other._relationTags,
              _relationTags,
            ) &&
            const DeepCollectionEquality().equals(other._moodTags, _moodTags) &&
            (identical(other.budgetTier, budgetTier) ||
                other.budgetTier == budgetTier) &&
            (identical(other.fulfillmentType, fulfillmentType) ||
                other.fulfillmentType == fulfillmentType) &&
            (identical(other.placeAddressText, placeAddressText) ||
                other.placeAddressText == placeAddressText) &&
            (identical(other.placeLat, placeLat) ||
                other.placeLat == placeLat) &&
            (identical(other.placeLng, placeLng) ||
                other.placeLng == placeLng) &&
            (identical(other.fulfillmentDate, fulfillmentDate) ||
                other.fulfillmentDate == fulfillmentDate) &&
            const DeepCollectionEquality().equals(
              other._requestedTimeSlots,
              _requestedTimeSlots,
            ));
  }

  @override
  int get hashCode => Object.hash(
    runtimeType,
    const DeepCollectionEquality().hash(_purposeTags),
    const DeepCollectionEquality().hash(_relationTags),
    const DeepCollectionEquality().hash(_moodTags),
    budgetTier,
    fulfillmentType,
    placeAddressText,
    placeLat,
    placeLng,
    fulfillmentDate,
    const DeepCollectionEquality().hash(_requestedTimeSlots),
  );

  /// Create a copy of RequestFormState
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$RequestFormStateImplCopyWith<_$RequestFormStateImpl> get copyWith =>
      __$$RequestFormStateImplCopyWithImpl<_$RequestFormStateImpl>(
        this,
        _$identity,
      );
}

abstract class _RequestFormState extends RequestFormState {
  const factory _RequestFormState({
    final List<String> purposeTags,
    final List<String> relationTags,
    final List<String> moodTags,
    final String? budgetTier,
    final String? fulfillmentType,
    final String? placeAddressText,
    final double? placeLat,
    final double? placeLng,
    final String? fulfillmentDate,
    final List<TimeSlot> requestedTimeSlots,
  }) = _$RequestFormStateImpl;
  const _RequestFormState._() : super._();

  // Step 1
  @override
  List<String> get purposeTags;
  @override
  List<String> get relationTags;
  @override
  List<String> get moodTags; // Step 2
  @override
  String? get budgetTier; // Step 3
  @override
  String? get fulfillmentType;
  @override
  String? get placeAddressText;
  @override
  double? get placeLat;
  @override
  double? get placeLng;
  @override
  String? get fulfillmentDate; // Step 4
  @override
  List<TimeSlot> get requestedTimeSlots;

  /// Create a copy of RequestFormState
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$RequestFormStateImplCopyWith<_$RequestFormStateImpl> get copyWith =>
      throw _privateConstructorUsedError;
}
