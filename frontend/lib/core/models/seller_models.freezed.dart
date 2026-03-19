// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'seller_models.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
  'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#adding-getters-and-methods-to-our-models',
);

SellerHomeData _$SellerHomeDataFromJson(Map<String, dynamic> json) {
  return _SellerHomeData.fromJson(json);
}

/// @nodoc
mixin _$SellerHomeData {
  int get openRequestCount => throw _privateConstructorUsedError;
  int get draftProposalCount => throw _privateConstructorUsedError;
  int get confirmedReservationCount => throw _privateConstructorUsedError;
  String get shopName => throw _privateConstructorUsedError;

  /// Serializes this SellerHomeData to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of SellerHomeData
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $SellerHomeDataCopyWith<SellerHomeData> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $SellerHomeDataCopyWith<$Res> {
  factory $SellerHomeDataCopyWith(
    SellerHomeData value,
    $Res Function(SellerHomeData) then,
  ) = _$SellerHomeDataCopyWithImpl<$Res, SellerHomeData>;
  @useResult
  $Res call({
    int openRequestCount,
    int draftProposalCount,
    int confirmedReservationCount,
    String shopName,
  });
}

/// @nodoc
class _$SellerHomeDataCopyWithImpl<$Res, $Val extends SellerHomeData>
    implements $SellerHomeDataCopyWith<$Res> {
  _$SellerHomeDataCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of SellerHomeData
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? openRequestCount = null,
    Object? draftProposalCount = null,
    Object? confirmedReservationCount = null,
    Object? shopName = null,
  }) {
    return _then(
      _value.copyWith(
            openRequestCount: null == openRequestCount
                ? _value.openRequestCount
                : openRequestCount // ignore: cast_nullable_to_non_nullable
                      as int,
            draftProposalCount: null == draftProposalCount
                ? _value.draftProposalCount
                : draftProposalCount // ignore: cast_nullable_to_non_nullable
                      as int,
            confirmedReservationCount: null == confirmedReservationCount
                ? _value.confirmedReservationCount
                : confirmedReservationCount // ignore: cast_nullable_to_non_nullable
                      as int,
            shopName: null == shopName
                ? _value.shopName
                : shopName // ignore: cast_nullable_to_non_nullable
                      as String,
          )
          as $Val,
    );
  }
}

/// @nodoc
abstract class _$$SellerHomeDataImplCopyWith<$Res>
    implements $SellerHomeDataCopyWith<$Res> {
  factory _$$SellerHomeDataImplCopyWith(
    _$SellerHomeDataImpl value,
    $Res Function(_$SellerHomeDataImpl) then,
  ) = __$$SellerHomeDataImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({
    int openRequestCount,
    int draftProposalCount,
    int confirmedReservationCount,
    String shopName,
  });
}

/// @nodoc
class __$$SellerHomeDataImplCopyWithImpl<$Res>
    extends _$SellerHomeDataCopyWithImpl<$Res, _$SellerHomeDataImpl>
    implements _$$SellerHomeDataImplCopyWith<$Res> {
  __$$SellerHomeDataImplCopyWithImpl(
    _$SellerHomeDataImpl _value,
    $Res Function(_$SellerHomeDataImpl) _then,
  ) : super(_value, _then);

  /// Create a copy of SellerHomeData
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? openRequestCount = null,
    Object? draftProposalCount = null,
    Object? confirmedReservationCount = null,
    Object? shopName = null,
  }) {
    return _then(
      _$SellerHomeDataImpl(
        openRequestCount: null == openRequestCount
            ? _value.openRequestCount
            : openRequestCount // ignore: cast_nullable_to_non_nullable
                  as int,
        draftProposalCount: null == draftProposalCount
            ? _value.draftProposalCount
            : draftProposalCount // ignore: cast_nullable_to_non_nullable
                  as int,
        confirmedReservationCount: null == confirmedReservationCount
            ? _value.confirmedReservationCount
            : confirmedReservationCount // ignore: cast_nullable_to_non_nullable
                  as int,
        shopName: null == shopName
            ? _value.shopName
            : shopName // ignore: cast_nullable_to_non_nullable
                  as String,
      ),
    );
  }
}

/// @nodoc
@JsonSerializable()
class _$SellerHomeDataImpl implements _SellerHomeData {
  const _$SellerHomeDataImpl({
    required this.openRequestCount,
    required this.draftProposalCount,
    required this.confirmedReservationCount,
    required this.shopName,
  });

  factory _$SellerHomeDataImpl.fromJson(Map<String, dynamic> json) =>
      _$$SellerHomeDataImplFromJson(json);

  @override
  final int openRequestCount;
  @override
  final int draftProposalCount;
  @override
  final int confirmedReservationCount;
  @override
  final String shopName;

  @override
  String toString() {
    return 'SellerHomeData(openRequestCount: $openRequestCount, draftProposalCount: $draftProposalCount, confirmedReservationCount: $confirmedReservationCount, shopName: $shopName)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$SellerHomeDataImpl &&
            (identical(other.openRequestCount, openRequestCount) ||
                other.openRequestCount == openRequestCount) &&
            (identical(other.draftProposalCount, draftProposalCount) ||
                other.draftProposalCount == draftProposalCount) &&
            (identical(
                  other.confirmedReservationCount,
                  confirmedReservationCount,
                ) ||
                other.confirmedReservationCount == confirmedReservationCount) &&
            (identical(other.shopName, shopName) ||
                other.shopName == shopName));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(
    runtimeType,
    openRequestCount,
    draftProposalCount,
    confirmedReservationCount,
    shopName,
  );

  /// Create a copy of SellerHomeData
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$SellerHomeDataImplCopyWith<_$SellerHomeDataImpl> get copyWith =>
      __$$SellerHomeDataImplCopyWithImpl<_$SellerHomeDataImpl>(
        this,
        _$identity,
      );

  @override
  Map<String, dynamic> toJson() {
    return _$$SellerHomeDataImplToJson(this);
  }
}

abstract class _SellerHomeData implements SellerHomeData {
  const factory _SellerHomeData({
    required final int openRequestCount,
    required final int draftProposalCount,
    required final int confirmedReservationCount,
    required final String shopName,
  }) = _$SellerHomeDataImpl;

  factory _SellerHomeData.fromJson(Map<String, dynamic> json) =
      _$SellerHomeDataImpl.fromJson;

  @override
  int get openRequestCount;
  @override
  int get draftProposalCount;
  @override
  int get confirmedReservationCount;
  @override
  String get shopName;

  /// Create a copy of SellerHomeData
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$SellerHomeDataImplCopyWith<_$SellerHomeDataImpl> get copyWith =>
      throw _privateConstructorUsedError;
}

SellerRequestSummary _$SellerRequestSummaryFromJson(Map<String, dynamic> json) {
  return _SellerRequestSummary.fromJson(json);
}

/// @nodoc
mixin _$SellerRequestSummary {
  int get requestId => throw _privateConstructorUsedError;
  String get status => throw _privateConstructorUsedError;
  String get budgetTier => throw _privateConstructorUsedError;
  String get fulfillmentType => throw _privateConstructorUsedError;
  String get fulfillmentDate => throw _privateConstructorUsedError;
  String get expiresAt => throw _privateConstructorUsedError;
  List<String> get purposeTags => throw _privateConstructorUsedError;
  List<String> get moodTags => throw _privateConstructorUsedError;
  String? get myProposalStatus => throw _privateConstructorUsedError;
  String? get distance => throw _privateConstructorUsedError;
  String? get slotLabel => throw _privateConstructorUsedError;

  /// Serializes this SellerRequestSummary to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of SellerRequestSummary
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $SellerRequestSummaryCopyWith<SellerRequestSummary> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $SellerRequestSummaryCopyWith<$Res> {
  factory $SellerRequestSummaryCopyWith(
    SellerRequestSummary value,
    $Res Function(SellerRequestSummary) then,
  ) = _$SellerRequestSummaryCopyWithImpl<$Res, SellerRequestSummary>;
  @useResult
  $Res call({
    int requestId,
    String status,
    String budgetTier,
    String fulfillmentType,
    String fulfillmentDate,
    String expiresAt,
    List<String> purposeTags,
    List<String> moodTags,
    String? myProposalStatus,
    String? distance,
    String? slotLabel,
  });
}

/// @nodoc
class _$SellerRequestSummaryCopyWithImpl<
  $Res,
  $Val extends SellerRequestSummary
>
    implements $SellerRequestSummaryCopyWith<$Res> {
  _$SellerRequestSummaryCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of SellerRequestSummary
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? requestId = null,
    Object? status = null,
    Object? budgetTier = null,
    Object? fulfillmentType = null,
    Object? fulfillmentDate = null,
    Object? expiresAt = null,
    Object? purposeTags = null,
    Object? moodTags = null,
    Object? myProposalStatus = freezed,
    Object? distance = freezed,
    Object? slotLabel = freezed,
  }) {
    return _then(
      _value.copyWith(
            requestId: null == requestId
                ? _value.requestId
                : requestId // ignore: cast_nullable_to_non_nullable
                      as int,
            status: null == status
                ? _value.status
                : status // ignore: cast_nullable_to_non_nullable
                      as String,
            budgetTier: null == budgetTier
                ? _value.budgetTier
                : budgetTier // ignore: cast_nullable_to_non_nullable
                      as String,
            fulfillmentType: null == fulfillmentType
                ? _value.fulfillmentType
                : fulfillmentType // ignore: cast_nullable_to_non_nullable
                      as String,
            fulfillmentDate: null == fulfillmentDate
                ? _value.fulfillmentDate
                : fulfillmentDate // ignore: cast_nullable_to_non_nullable
                      as String,
            expiresAt: null == expiresAt
                ? _value.expiresAt
                : expiresAt // ignore: cast_nullable_to_non_nullable
                      as String,
            purposeTags: null == purposeTags
                ? _value.purposeTags
                : purposeTags // ignore: cast_nullable_to_non_nullable
                      as List<String>,
            moodTags: null == moodTags
                ? _value.moodTags
                : moodTags // ignore: cast_nullable_to_non_nullable
                      as List<String>,
            myProposalStatus: freezed == myProposalStatus
                ? _value.myProposalStatus
                : myProposalStatus // ignore: cast_nullable_to_non_nullable
                      as String?,
            distance: freezed == distance
                ? _value.distance
                : distance // ignore: cast_nullable_to_non_nullable
                      as String?,
            slotLabel: freezed == slotLabel
                ? _value.slotLabel
                : slotLabel // ignore: cast_nullable_to_non_nullable
                      as String?,
          )
          as $Val,
    );
  }
}

/// @nodoc
abstract class _$$SellerRequestSummaryImplCopyWith<$Res>
    implements $SellerRequestSummaryCopyWith<$Res> {
  factory _$$SellerRequestSummaryImplCopyWith(
    _$SellerRequestSummaryImpl value,
    $Res Function(_$SellerRequestSummaryImpl) then,
  ) = __$$SellerRequestSummaryImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({
    int requestId,
    String status,
    String budgetTier,
    String fulfillmentType,
    String fulfillmentDate,
    String expiresAt,
    List<String> purposeTags,
    List<String> moodTags,
    String? myProposalStatus,
    String? distance,
    String? slotLabel,
  });
}

/// @nodoc
class __$$SellerRequestSummaryImplCopyWithImpl<$Res>
    extends _$SellerRequestSummaryCopyWithImpl<$Res, _$SellerRequestSummaryImpl>
    implements _$$SellerRequestSummaryImplCopyWith<$Res> {
  __$$SellerRequestSummaryImplCopyWithImpl(
    _$SellerRequestSummaryImpl _value,
    $Res Function(_$SellerRequestSummaryImpl) _then,
  ) : super(_value, _then);

  /// Create a copy of SellerRequestSummary
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? requestId = null,
    Object? status = null,
    Object? budgetTier = null,
    Object? fulfillmentType = null,
    Object? fulfillmentDate = null,
    Object? expiresAt = null,
    Object? purposeTags = null,
    Object? moodTags = null,
    Object? myProposalStatus = freezed,
    Object? distance = freezed,
    Object? slotLabel = freezed,
  }) {
    return _then(
      _$SellerRequestSummaryImpl(
        requestId: null == requestId
            ? _value.requestId
            : requestId // ignore: cast_nullable_to_non_nullable
                  as int,
        status: null == status
            ? _value.status
            : status // ignore: cast_nullable_to_non_nullable
                  as String,
        budgetTier: null == budgetTier
            ? _value.budgetTier
            : budgetTier // ignore: cast_nullable_to_non_nullable
                  as String,
        fulfillmentType: null == fulfillmentType
            ? _value.fulfillmentType
            : fulfillmentType // ignore: cast_nullable_to_non_nullable
                  as String,
        fulfillmentDate: null == fulfillmentDate
            ? _value.fulfillmentDate
            : fulfillmentDate // ignore: cast_nullable_to_non_nullable
                  as String,
        expiresAt: null == expiresAt
            ? _value.expiresAt
            : expiresAt // ignore: cast_nullable_to_non_nullable
                  as String,
        purposeTags: null == purposeTags
            ? _value._purposeTags
            : purposeTags // ignore: cast_nullable_to_non_nullable
                  as List<String>,
        moodTags: null == moodTags
            ? _value._moodTags
            : moodTags // ignore: cast_nullable_to_non_nullable
                  as List<String>,
        myProposalStatus: freezed == myProposalStatus
            ? _value.myProposalStatus
            : myProposalStatus // ignore: cast_nullable_to_non_nullable
                  as String?,
        distance: freezed == distance
            ? _value.distance
            : distance // ignore: cast_nullable_to_non_nullable
                  as String?,
        slotLabel: freezed == slotLabel
            ? _value.slotLabel
            : slotLabel // ignore: cast_nullable_to_non_nullable
                  as String?,
      ),
    );
  }
}

/// @nodoc
@JsonSerializable()
class _$SellerRequestSummaryImpl implements _SellerRequestSummary {
  const _$SellerRequestSummaryImpl({
    required this.requestId,
    required this.status,
    required this.budgetTier,
    required this.fulfillmentType,
    required this.fulfillmentDate,
    required this.expiresAt,
    final List<String> purposeTags = const [],
    final List<String> moodTags = const [],
    this.myProposalStatus,
    this.distance,
    this.slotLabel,
  }) : _purposeTags = purposeTags,
       _moodTags = moodTags;

  factory _$SellerRequestSummaryImpl.fromJson(Map<String, dynamic> json) =>
      _$$SellerRequestSummaryImplFromJson(json);

  @override
  final int requestId;
  @override
  final String status;
  @override
  final String budgetTier;
  @override
  final String fulfillmentType;
  @override
  final String fulfillmentDate;
  @override
  final String expiresAt;
  final List<String> _purposeTags;
  @override
  @JsonKey()
  List<String> get purposeTags {
    if (_purposeTags is EqualUnmodifiableListView) return _purposeTags;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableListView(_purposeTags);
  }

  final List<String> _moodTags;
  @override
  @JsonKey()
  List<String> get moodTags {
    if (_moodTags is EqualUnmodifiableListView) return _moodTags;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableListView(_moodTags);
  }

  @override
  final String? myProposalStatus;
  @override
  final String? distance;
  @override
  final String? slotLabel;

  @override
  String toString() {
    return 'SellerRequestSummary(requestId: $requestId, status: $status, budgetTier: $budgetTier, fulfillmentType: $fulfillmentType, fulfillmentDate: $fulfillmentDate, expiresAt: $expiresAt, purposeTags: $purposeTags, moodTags: $moodTags, myProposalStatus: $myProposalStatus, distance: $distance, slotLabel: $slotLabel)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$SellerRequestSummaryImpl &&
            (identical(other.requestId, requestId) ||
                other.requestId == requestId) &&
            (identical(other.status, status) || other.status == status) &&
            (identical(other.budgetTier, budgetTier) ||
                other.budgetTier == budgetTier) &&
            (identical(other.fulfillmentType, fulfillmentType) ||
                other.fulfillmentType == fulfillmentType) &&
            (identical(other.fulfillmentDate, fulfillmentDate) ||
                other.fulfillmentDate == fulfillmentDate) &&
            (identical(other.expiresAt, expiresAt) ||
                other.expiresAt == expiresAt) &&
            const DeepCollectionEquality().equals(
              other._purposeTags,
              _purposeTags,
            ) &&
            const DeepCollectionEquality().equals(other._moodTags, _moodTags) &&
            (identical(other.myProposalStatus, myProposalStatus) ||
                other.myProposalStatus == myProposalStatus) &&
            (identical(other.distance, distance) ||
                other.distance == distance) &&
            (identical(other.slotLabel, slotLabel) ||
                other.slotLabel == slotLabel));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(
    runtimeType,
    requestId,
    status,
    budgetTier,
    fulfillmentType,
    fulfillmentDate,
    expiresAt,
    const DeepCollectionEquality().hash(_purposeTags),
    const DeepCollectionEquality().hash(_moodTags),
    myProposalStatus,
    distance,
    slotLabel,
  );

  /// Create a copy of SellerRequestSummary
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$SellerRequestSummaryImplCopyWith<_$SellerRequestSummaryImpl>
  get copyWith =>
      __$$SellerRequestSummaryImplCopyWithImpl<_$SellerRequestSummaryImpl>(
        this,
        _$identity,
      );

  @override
  Map<String, dynamic> toJson() {
    return _$$SellerRequestSummaryImplToJson(this);
  }
}

abstract class _SellerRequestSummary implements SellerRequestSummary {
  const factory _SellerRequestSummary({
    required final int requestId,
    required final String status,
    required final String budgetTier,
    required final String fulfillmentType,
    required final String fulfillmentDate,
    required final String expiresAt,
    final List<String> purposeTags,
    final List<String> moodTags,
    final String? myProposalStatus,
    final String? distance,
    final String? slotLabel,
  }) = _$SellerRequestSummaryImpl;

  factory _SellerRequestSummary.fromJson(Map<String, dynamic> json) =
      _$SellerRequestSummaryImpl.fromJson;

  @override
  int get requestId;
  @override
  String get status;
  @override
  String get budgetTier;
  @override
  String get fulfillmentType;
  @override
  String get fulfillmentDate;
  @override
  String get expiresAt;
  @override
  List<String> get purposeTags;
  @override
  List<String> get moodTags;
  @override
  String? get myProposalStatus;
  @override
  String? get distance;
  @override
  String? get slotLabel;

  /// Create a copy of SellerRequestSummary
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$SellerRequestSummaryImplCopyWith<_$SellerRequestSummaryImpl>
  get copyWith => throw _privateConstructorUsedError;
}

SellerRequestDetail _$SellerRequestDetailFromJson(Map<String, dynamic> json) {
  return _SellerRequestDetail.fromJson(json);
}

/// @nodoc
mixin _$SellerRequestDetail {
  int get requestId => throw _privateConstructorUsedError;
  String get status => throw _privateConstructorUsedError;
  String get budgetTier => throw _privateConstructorUsedError;
  String get fulfillmentType => throw _privateConstructorUsedError;
  String get fulfillmentDate => throw _privateConstructorUsedError;
  String get expiresAt => throw _privateConstructorUsedError;
  String get placeAddressText => throw _privateConstructorUsedError;
  List<String> get purposeTags => throw _privateConstructorUsedError;
  List<String> get relationTags => throw _privateConstructorUsedError;
  List<String> get moodTags => throw _privateConstructorUsedError;
  List<Map<String, String>> get requestedTimeSlots =>
      throw _privateConstructorUsedError;
  String? get myProposalId => throw _privateConstructorUsedError;
  String? get distance => throw _privateConstructorUsedError;

  /// Serializes this SellerRequestDetail to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of SellerRequestDetail
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $SellerRequestDetailCopyWith<SellerRequestDetail> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $SellerRequestDetailCopyWith<$Res> {
  factory $SellerRequestDetailCopyWith(
    SellerRequestDetail value,
    $Res Function(SellerRequestDetail) then,
  ) = _$SellerRequestDetailCopyWithImpl<$Res, SellerRequestDetail>;
  @useResult
  $Res call({
    int requestId,
    String status,
    String budgetTier,
    String fulfillmentType,
    String fulfillmentDate,
    String expiresAt,
    String placeAddressText,
    List<String> purposeTags,
    List<String> relationTags,
    List<String> moodTags,
    List<Map<String, String>> requestedTimeSlots,
    String? myProposalId,
    String? distance,
  });
}

/// @nodoc
class _$SellerRequestDetailCopyWithImpl<$Res, $Val extends SellerRequestDetail>
    implements $SellerRequestDetailCopyWith<$Res> {
  _$SellerRequestDetailCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of SellerRequestDetail
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? requestId = null,
    Object? status = null,
    Object? budgetTier = null,
    Object? fulfillmentType = null,
    Object? fulfillmentDate = null,
    Object? expiresAt = null,
    Object? placeAddressText = null,
    Object? purposeTags = null,
    Object? relationTags = null,
    Object? moodTags = null,
    Object? requestedTimeSlots = null,
    Object? myProposalId = freezed,
    Object? distance = freezed,
  }) {
    return _then(
      _value.copyWith(
            requestId: null == requestId
                ? _value.requestId
                : requestId // ignore: cast_nullable_to_non_nullable
                      as int,
            status: null == status
                ? _value.status
                : status // ignore: cast_nullable_to_non_nullable
                      as String,
            budgetTier: null == budgetTier
                ? _value.budgetTier
                : budgetTier // ignore: cast_nullable_to_non_nullable
                      as String,
            fulfillmentType: null == fulfillmentType
                ? _value.fulfillmentType
                : fulfillmentType // ignore: cast_nullable_to_non_nullable
                      as String,
            fulfillmentDate: null == fulfillmentDate
                ? _value.fulfillmentDate
                : fulfillmentDate // ignore: cast_nullable_to_non_nullable
                      as String,
            expiresAt: null == expiresAt
                ? _value.expiresAt
                : expiresAt // ignore: cast_nullable_to_non_nullable
                      as String,
            placeAddressText: null == placeAddressText
                ? _value.placeAddressText
                : placeAddressText // ignore: cast_nullable_to_non_nullable
                      as String,
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
            requestedTimeSlots: null == requestedTimeSlots
                ? _value.requestedTimeSlots
                : requestedTimeSlots // ignore: cast_nullable_to_non_nullable
                      as List<Map<String, String>>,
            myProposalId: freezed == myProposalId
                ? _value.myProposalId
                : myProposalId // ignore: cast_nullable_to_non_nullable
                      as String?,
            distance: freezed == distance
                ? _value.distance
                : distance // ignore: cast_nullable_to_non_nullable
                      as String?,
          )
          as $Val,
    );
  }
}

/// @nodoc
abstract class _$$SellerRequestDetailImplCopyWith<$Res>
    implements $SellerRequestDetailCopyWith<$Res> {
  factory _$$SellerRequestDetailImplCopyWith(
    _$SellerRequestDetailImpl value,
    $Res Function(_$SellerRequestDetailImpl) then,
  ) = __$$SellerRequestDetailImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({
    int requestId,
    String status,
    String budgetTier,
    String fulfillmentType,
    String fulfillmentDate,
    String expiresAt,
    String placeAddressText,
    List<String> purposeTags,
    List<String> relationTags,
    List<String> moodTags,
    List<Map<String, String>> requestedTimeSlots,
    String? myProposalId,
    String? distance,
  });
}

/// @nodoc
class __$$SellerRequestDetailImplCopyWithImpl<$Res>
    extends _$SellerRequestDetailCopyWithImpl<$Res, _$SellerRequestDetailImpl>
    implements _$$SellerRequestDetailImplCopyWith<$Res> {
  __$$SellerRequestDetailImplCopyWithImpl(
    _$SellerRequestDetailImpl _value,
    $Res Function(_$SellerRequestDetailImpl) _then,
  ) : super(_value, _then);

  /// Create a copy of SellerRequestDetail
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? requestId = null,
    Object? status = null,
    Object? budgetTier = null,
    Object? fulfillmentType = null,
    Object? fulfillmentDate = null,
    Object? expiresAt = null,
    Object? placeAddressText = null,
    Object? purposeTags = null,
    Object? relationTags = null,
    Object? moodTags = null,
    Object? requestedTimeSlots = null,
    Object? myProposalId = freezed,
    Object? distance = freezed,
  }) {
    return _then(
      _$SellerRequestDetailImpl(
        requestId: null == requestId
            ? _value.requestId
            : requestId // ignore: cast_nullable_to_non_nullable
                  as int,
        status: null == status
            ? _value.status
            : status // ignore: cast_nullable_to_non_nullable
                  as String,
        budgetTier: null == budgetTier
            ? _value.budgetTier
            : budgetTier // ignore: cast_nullable_to_non_nullable
                  as String,
        fulfillmentType: null == fulfillmentType
            ? _value.fulfillmentType
            : fulfillmentType // ignore: cast_nullable_to_non_nullable
                  as String,
        fulfillmentDate: null == fulfillmentDate
            ? _value.fulfillmentDate
            : fulfillmentDate // ignore: cast_nullable_to_non_nullable
                  as String,
        expiresAt: null == expiresAt
            ? _value.expiresAt
            : expiresAt // ignore: cast_nullable_to_non_nullable
                  as String,
        placeAddressText: null == placeAddressText
            ? _value.placeAddressText
            : placeAddressText // ignore: cast_nullable_to_non_nullable
                  as String,
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
        requestedTimeSlots: null == requestedTimeSlots
            ? _value._requestedTimeSlots
            : requestedTimeSlots // ignore: cast_nullable_to_non_nullable
                  as List<Map<String, String>>,
        myProposalId: freezed == myProposalId
            ? _value.myProposalId
            : myProposalId // ignore: cast_nullable_to_non_nullable
                  as String?,
        distance: freezed == distance
            ? _value.distance
            : distance // ignore: cast_nullable_to_non_nullable
                  as String?,
      ),
    );
  }
}

/// @nodoc
@JsonSerializable()
class _$SellerRequestDetailImpl implements _SellerRequestDetail {
  const _$SellerRequestDetailImpl({
    required this.requestId,
    required this.status,
    required this.budgetTier,
    required this.fulfillmentType,
    required this.fulfillmentDate,
    required this.expiresAt,
    required this.placeAddressText,
    final List<String> purposeTags = const [],
    final List<String> relationTags = const [],
    final List<String> moodTags = const [],
    final List<Map<String, String>> requestedTimeSlots = const [],
    this.myProposalId,
    this.distance,
  }) : _purposeTags = purposeTags,
       _relationTags = relationTags,
       _moodTags = moodTags,
       _requestedTimeSlots = requestedTimeSlots;

  factory _$SellerRequestDetailImpl.fromJson(Map<String, dynamic> json) =>
      _$$SellerRequestDetailImplFromJson(json);

  @override
  final int requestId;
  @override
  final String status;
  @override
  final String budgetTier;
  @override
  final String fulfillmentType;
  @override
  final String fulfillmentDate;
  @override
  final String expiresAt;
  @override
  final String placeAddressText;
  final List<String> _purposeTags;
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

  final List<Map<String, String>> _requestedTimeSlots;
  @override
  @JsonKey()
  List<Map<String, String>> get requestedTimeSlots {
    if (_requestedTimeSlots is EqualUnmodifiableListView)
      return _requestedTimeSlots;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableListView(_requestedTimeSlots);
  }

  @override
  final String? myProposalId;
  @override
  final String? distance;

  @override
  String toString() {
    return 'SellerRequestDetail(requestId: $requestId, status: $status, budgetTier: $budgetTier, fulfillmentType: $fulfillmentType, fulfillmentDate: $fulfillmentDate, expiresAt: $expiresAt, placeAddressText: $placeAddressText, purposeTags: $purposeTags, relationTags: $relationTags, moodTags: $moodTags, requestedTimeSlots: $requestedTimeSlots, myProposalId: $myProposalId, distance: $distance)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$SellerRequestDetailImpl &&
            (identical(other.requestId, requestId) ||
                other.requestId == requestId) &&
            (identical(other.status, status) || other.status == status) &&
            (identical(other.budgetTier, budgetTier) ||
                other.budgetTier == budgetTier) &&
            (identical(other.fulfillmentType, fulfillmentType) ||
                other.fulfillmentType == fulfillmentType) &&
            (identical(other.fulfillmentDate, fulfillmentDate) ||
                other.fulfillmentDate == fulfillmentDate) &&
            (identical(other.expiresAt, expiresAt) ||
                other.expiresAt == expiresAt) &&
            (identical(other.placeAddressText, placeAddressText) ||
                other.placeAddressText == placeAddressText) &&
            const DeepCollectionEquality().equals(
              other._purposeTags,
              _purposeTags,
            ) &&
            const DeepCollectionEquality().equals(
              other._relationTags,
              _relationTags,
            ) &&
            const DeepCollectionEquality().equals(other._moodTags, _moodTags) &&
            const DeepCollectionEquality().equals(
              other._requestedTimeSlots,
              _requestedTimeSlots,
            ) &&
            (identical(other.myProposalId, myProposalId) ||
                other.myProposalId == myProposalId) &&
            (identical(other.distance, distance) ||
                other.distance == distance));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(
    runtimeType,
    requestId,
    status,
    budgetTier,
    fulfillmentType,
    fulfillmentDate,
    expiresAt,
    placeAddressText,
    const DeepCollectionEquality().hash(_purposeTags),
    const DeepCollectionEquality().hash(_relationTags),
    const DeepCollectionEquality().hash(_moodTags),
    const DeepCollectionEquality().hash(_requestedTimeSlots),
    myProposalId,
    distance,
  );

  /// Create a copy of SellerRequestDetail
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$SellerRequestDetailImplCopyWith<_$SellerRequestDetailImpl> get copyWith =>
      __$$SellerRequestDetailImplCopyWithImpl<_$SellerRequestDetailImpl>(
        this,
        _$identity,
      );

  @override
  Map<String, dynamic> toJson() {
    return _$$SellerRequestDetailImplToJson(this);
  }
}

abstract class _SellerRequestDetail implements SellerRequestDetail {
  const factory _SellerRequestDetail({
    required final int requestId,
    required final String status,
    required final String budgetTier,
    required final String fulfillmentType,
    required final String fulfillmentDate,
    required final String expiresAt,
    required final String placeAddressText,
    final List<String> purposeTags,
    final List<String> relationTags,
    final List<String> moodTags,
    final List<Map<String, String>> requestedTimeSlots,
    final String? myProposalId,
    final String? distance,
  }) = _$SellerRequestDetailImpl;

  factory _SellerRequestDetail.fromJson(Map<String, dynamic> json) =
      _$SellerRequestDetailImpl.fromJson;

  @override
  int get requestId;
  @override
  String get status;
  @override
  String get budgetTier;
  @override
  String get fulfillmentType;
  @override
  String get fulfillmentDate;
  @override
  String get expiresAt;
  @override
  String get placeAddressText;
  @override
  List<String> get purposeTags;
  @override
  List<String> get relationTags;
  @override
  List<String> get moodTags;
  @override
  List<Map<String, String>> get requestedTimeSlots;
  @override
  String? get myProposalId;
  @override
  String? get distance;

  /// Create a copy of SellerRequestDetail
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$SellerRequestDetailImplCopyWith<_$SellerRequestDetailImpl> get copyWith =>
      throw _privateConstructorUsedError;
}

SellerReservationDetail _$SellerReservationDetailFromJson(
  Map<String, dynamic> json,
) {
  return _SellerReservationDetail.fromJson(json);
}

/// @nodoc
mixin _$SellerReservationDetail {
  int get reservationId => throw _privateConstructorUsedError;
  String get status => throw _privateConstructorUsedError;
  String get buyerNickName => throw _privateConstructorUsedError;
  String get conceptTitle => throw _privateConstructorUsedError;
  String get description => throw _privateConstructorUsedError;
  int get price => throw _privateConstructorUsedError;
  String get fulfillmentType => throw _privateConstructorUsedError;
  String get fulfillmentDate => throw _privateConstructorUsedError;
  String get fulfillmentSlotKind => throw _privateConstructorUsedError;
  String get fulfillmentSlotValue => throw _privateConstructorUsedError;
  String get placeAddressText => throw _privateConstructorUsedError;
  String get confirmedAt => throw _privateConstructorUsedError;
  List<String> get imageUrls => throw _privateConstructorUsedError;
  List<String> get purposeTags => throw _privateConstructorUsedError;
  List<String> get relationTags => throw _privateConstructorUsedError;
  List<String> get moodTags => throw _privateConstructorUsedError;
  String? get budgetTier => throw _privateConstructorUsedError;

  /// Serializes this SellerReservationDetail to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of SellerReservationDetail
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $SellerReservationDetailCopyWith<SellerReservationDetail> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $SellerReservationDetailCopyWith<$Res> {
  factory $SellerReservationDetailCopyWith(
    SellerReservationDetail value,
    $Res Function(SellerReservationDetail) then,
  ) = _$SellerReservationDetailCopyWithImpl<$Res, SellerReservationDetail>;
  @useResult
  $Res call({
    int reservationId,
    String status,
    String buyerNickName,
    String conceptTitle,
    String description,
    int price,
    String fulfillmentType,
    String fulfillmentDate,
    String fulfillmentSlotKind,
    String fulfillmentSlotValue,
    String placeAddressText,
    String confirmedAt,
    List<String> imageUrls,
    List<String> purposeTags,
    List<String> relationTags,
    List<String> moodTags,
    String? budgetTier,
  });
}

/// @nodoc
class _$SellerReservationDetailCopyWithImpl<
  $Res,
  $Val extends SellerReservationDetail
>
    implements $SellerReservationDetailCopyWith<$Res> {
  _$SellerReservationDetailCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of SellerReservationDetail
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? reservationId = null,
    Object? status = null,
    Object? buyerNickName = null,
    Object? conceptTitle = null,
    Object? description = null,
    Object? price = null,
    Object? fulfillmentType = null,
    Object? fulfillmentDate = null,
    Object? fulfillmentSlotKind = null,
    Object? fulfillmentSlotValue = null,
    Object? placeAddressText = null,
    Object? confirmedAt = null,
    Object? imageUrls = null,
    Object? purposeTags = null,
    Object? relationTags = null,
    Object? moodTags = null,
    Object? budgetTier = freezed,
  }) {
    return _then(
      _value.copyWith(
            reservationId: null == reservationId
                ? _value.reservationId
                : reservationId // ignore: cast_nullable_to_non_nullable
                      as int,
            status: null == status
                ? _value.status
                : status // ignore: cast_nullable_to_non_nullable
                      as String,
            buyerNickName: null == buyerNickName
                ? _value.buyerNickName
                : buyerNickName // ignore: cast_nullable_to_non_nullable
                      as String,
            conceptTitle: null == conceptTitle
                ? _value.conceptTitle
                : conceptTitle // ignore: cast_nullable_to_non_nullable
                      as String,
            description: null == description
                ? _value.description
                : description // ignore: cast_nullable_to_non_nullable
                      as String,
            price: null == price
                ? _value.price
                : price // ignore: cast_nullable_to_non_nullable
                      as int,
            fulfillmentType: null == fulfillmentType
                ? _value.fulfillmentType
                : fulfillmentType // ignore: cast_nullable_to_non_nullable
                      as String,
            fulfillmentDate: null == fulfillmentDate
                ? _value.fulfillmentDate
                : fulfillmentDate // ignore: cast_nullable_to_non_nullable
                      as String,
            fulfillmentSlotKind: null == fulfillmentSlotKind
                ? _value.fulfillmentSlotKind
                : fulfillmentSlotKind // ignore: cast_nullable_to_non_nullable
                      as String,
            fulfillmentSlotValue: null == fulfillmentSlotValue
                ? _value.fulfillmentSlotValue
                : fulfillmentSlotValue // ignore: cast_nullable_to_non_nullable
                      as String,
            placeAddressText: null == placeAddressText
                ? _value.placeAddressText
                : placeAddressText // ignore: cast_nullable_to_non_nullable
                      as String,
            confirmedAt: null == confirmedAt
                ? _value.confirmedAt
                : confirmedAt // ignore: cast_nullable_to_non_nullable
                      as String,
            imageUrls: null == imageUrls
                ? _value.imageUrls
                : imageUrls // ignore: cast_nullable_to_non_nullable
                      as List<String>,
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
          )
          as $Val,
    );
  }
}

/// @nodoc
abstract class _$$SellerReservationDetailImplCopyWith<$Res>
    implements $SellerReservationDetailCopyWith<$Res> {
  factory _$$SellerReservationDetailImplCopyWith(
    _$SellerReservationDetailImpl value,
    $Res Function(_$SellerReservationDetailImpl) then,
  ) = __$$SellerReservationDetailImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({
    int reservationId,
    String status,
    String buyerNickName,
    String conceptTitle,
    String description,
    int price,
    String fulfillmentType,
    String fulfillmentDate,
    String fulfillmentSlotKind,
    String fulfillmentSlotValue,
    String placeAddressText,
    String confirmedAt,
    List<String> imageUrls,
    List<String> purposeTags,
    List<String> relationTags,
    List<String> moodTags,
    String? budgetTier,
  });
}

/// @nodoc
class __$$SellerReservationDetailImplCopyWithImpl<$Res>
    extends
        _$SellerReservationDetailCopyWithImpl<
          $Res,
          _$SellerReservationDetailImpl
        >
    implements _$$SellerReservationDetailImplCopyWith<$Res> {
  __$$SellerReservationDetailImplCopyWithImpl(
    _$SellerReservationDetailImpl _value,
    $Res Function(_$SellerReservationDetailImpl) _then,
  ) : super(_value, _then);

  /// Create a copy of SellerReservationDetail
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? reservationId = null,
    Object? status = null,
    Object? buyerNickName = null,
    Object? conceptTitle = null,
    Object? description = null,
    Object? price = null,
    Object? fulfillmentType = null,
    Object? fulfillmentDate = null,
    Object? fulfillmentSlotKind = null,
    Object? fulfillmentSlotValue = null,
    Object? placeAddressText = null,
    Object? confirmedAt = null,
    Object? imageUrls = null,
    Object? purposeTags = null,
    Object? relationTags = null,
    Object? moodTags = null,
    Object? budgetTier = freezed,
  }) {
    return _then(
      _$SellerReservationDetailImpl(
        reservationId: null == reservationId
            ? _value.reservationId
            : reservationId // ignore: cast_nullable_to_non_nullable
                  as int,
        status: null == status
            ? _value.status
            : status // ignore: cast_nullable_to_non_nullable
                  as String,
        buyerNickName: null == buyerNickName
            ? _value.buyerNickName
            : buyerNickName // ignore: cast_nullable_to_non_nullable
                  as String,
        conceptTitle: null == conceptTitle
            ? _value.conceptTitle
            : conceptTitle // ignore: cast_nullable_to_non_nullable
                  as String,
        description: null == description
            ? _value.description
            : description // ignore: cast_nullable_to_non_nullable
                  as String,
        price: null == price
            ? _value.price
            : price // ignore: cast_nullable_to_non_nullable
                  as int,
        fulfillmentType: null == fulfillmentType
            ? _value.fulfillmentType
            : fulfillmentType // ignore: cast_nullable_to_non_nullable
                  as String,
        fulfillmentDate: null == fulfillmentDate
            ? _value.fulfillmentDate
            : fulfillmentDate // ignore: cast_nullable_to_non_nullable
                  as String,
        fulfillmentSlotKind: null == fulfillmentSlotKind
            ? _value.fulfillmentSlotKind
            : fulfillmentSlotKind // ignore: cast_nullable_to_non_nullable
                  as String,
        fulfillmentSlotValue: null == fulfillmentSlotValue
            ? _value.fulfillmentSlotValue
            : fulfillmentSlotValue // ignore: cast_nullable_to_non_nullable
                  as String,
        placeAddressText: null == placeAddressText
            ? _value.placeAddressText
            : placeAddressText // ignore: cast_nullable_to_non_nullable
                  as String,
        confirmedAt: null == confirmedAt
            ? _value.confirmedAt
            : confirmedAt // ignore: cast_nullable_to_non_nullable
                  as String,
        imageUrls: null == imageUrls
            ? _value._imageUrls
            : imageUrls // ignore: cast_nullable_to_non_nullable
                  as List<String>,
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
      ),
    );
  }
}

/// @nodoc
@JsonSerializable()
class _$SellerReservationDetailImpl implements _SellerReservationDetail {
  const _$SellerReservationDetailImpl({
    required this.reservationId,
    required this.status,
    required this.buyerNickName,
    required this.conceptTitle,
    required this.description,
    required this.price,
    required this.fulfillmentType,
    required this.fulfillmentDate,
    required this.fulfillmentSlotKind,
    required this.fulfillmentSlotValue,
    required this.placeAddressText,
    required this.confirmedAt,
    final List<String> imageUrls = const [],
    final List<String> purposeTags = const [],
    final List<String> relationTags = const [],
    final List<String> moodTags = const [],
    this.budgetTier,
  }) : _imageUrls = imageUrls,
       _purposeTags = purposeTags,
       _relationTags = relationTags,
       _moodTags = moodTags;

  factory _$SellerReservationDetailImpl.fromJson(Map<String, dynamic> json) =>
      _$$SellerReservationDetailImplFromJson(json);

  @override
  final int reservationId;
  @override
  final String status;
  @override
  final String buyerNickName;
  @override
  final String conceptTitle;
  @override
  final String description;
  @override
  final int price;
  @override
  final String fulfillmentType;
  @override
  final String fulfillmentDate;
  @override
  final String fulfillmentSlotKind;
  @override
  final String fulfillmentSlotValue;
  @override
  final String placeAddressText;
  @override
  final String confirmedAt;
  final List<String> _imageUrls;
  @override
  @JsonKey()
  List<String> get imageUrls {
    if (_imageUrls is EqualUnmodifiableListView) return _imageUrls;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableListView(_imageUrls);
  }

  final List<String> _purposeTags;
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

  @override
  final String? budgetTier;

  @override
  String toString() {
    return 'SellerReservationDetail(reservationId: $reservationId, status: $status, buyerNickName: $buyerNickName, conceptTitle: $conceptTitle, description: $description, price: $price, fulfillmentType: $fulfillmentType, fulfillmentDate: $fulfillmentDate, fulfillmentSlotKind: $fulfillmentSlotKind, fulfillmentSlotValue: $fulfillmentSlotValue, placeAddressText: $placeAddressText, confirmedAt: $confirmedAt, imageUrls: $imageUrls, purposeTags: $purposeTags, relationTags: $relationTags, moodTags: $moodTags, budgetTier: $budgetTier)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$SellerReservationDetailImpl &&
            (identical(other.reservationId, reservationId) ||
                other.reservationId == reservationId) &&
            (identical(other.status, status) || other.status == status) &&
            (identical(other.buyerNickName, buyerNickName) ||
                other.buyerNickName == buyerNickName) &&
            (identical(other.conceptTitle, conceptTitle) ||
                other.conceptTitle == conceptTitle) &&
            (identical(other.description, description) ||
                other.description == description) &&
            (identical(other.price, price) || other.price == price) &&
            (identical(other.fulfillmentType, fulfillmentType) ||
                other.fulfillmentType == fulfillmentType) &&
            (identical(other.fulfillmentDate, fulfillmentDate) ||
                other.fulfillmentDate == fulfillmentDate) &&
            (identical(other.fulfillmentSlotKind, fulfillmentSlotKind) ||
                other.fulfillmentSlotKind == fulfillmentSlotKind) &&
            (identical(other.fulfillmentSlotValue, fulfillmentSlotValue) ||
                other.fulfillmentSlotValue == fulfillmentSlotValue) &&
            (identical(other.placeAddressText, placeAddressText) ||
                other.placeAddressText == placeAddressText) &&
            (identical(other.confirmedAt, confirmedAt) ||
                other.confirmedAt == confirmedAt) &&
            const DeepCollectionEquality().equals(
              other._imageUrls,
              _imageUrls,
            ) &&
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
                other.budgetTier == budgetTier));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(
    runtimeType,
    reservationId,
    status,
    buyerNickName,
    conceptTitle,
    description,
    price,
    fulfillmentType,
    fulfillmentDate,
    fulfillmentSlotKind,
    fulfillmentSlotValue,
    placeAddressText,
    confirmedAt,
    const DeepCollectionEquality().hash(_imageUrls),
    const DeepCollectionEquality().hash(_purposeTags),
    const DeepCollectionEquality().hash(_relationTags),
    const DeepCollectionEquality().hash(_moodTags),
    budgetTier,
  );

  /// Create a copy of SellerReservationDetail
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$SellerReservationDetailImplCopyWith<_$SellerReservationDetailImpl>
  get copyWith =>
      __$$SellerReservationDetailImplCopyWithImpl<
        _$SellerReservationDetailImpl
      >(this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$SellerReservationDetailImplToJson(this);
  }
}

abstract class _SellerReservationDetail implements SellerReservationDetail {
  const factory _SellerReservationDetail({
    required final int reservationId,
    required final String status,
    required final String buyerNickName,
    required final String conceptTitle,
    required final String description,
    required final int price,
    required final String fulfillmentType,
    required final String fulfillmentDate,
    required final String fulfillmentSlotKind,
    required final String fulfillmentSlotValue,
    required final String placeAddressText,
    required final String confirmedAt,
    final List<String> imageUrls,
    final List<String> purposeTags,
    final List<String> relationTags,
    final List<String> moodTags,
    final String? budgetTier,
  }) = _$SellerReservationDetailImpl;

  factory _SellerReservationDetail.fromJson(Map<String, dynamic> json) =
      _$SellerReservationDetailImpl.fromJson;

  @override
  int get reservationId;
  @override
  String get status;
  @override
  String get buyerNickName;
  @override
  String get conceptTitle;
  @override
  String get description;
  @override
  int get price;
  @override
  String get fulfillmentType;
  @override
  String get fulfillmentDate;
  @override
  String get fulfillmentSlotKind;
  @override
  String get fulfillmentSlotValue;
  @override
  String get placeAddressText;
  @override
  String get confirmedAt;
  @override
  List<String> get imageUrls;
  @override
  List<String> get purposeTags;
  @override
  List<String> get relationTags;
  @override
  List<String> get moodTags;
  @override
  String? get budgetTier;

  /// Create a copy of SellerReservationDetail
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$SellerReservationDetailImplCopyWith<_$SellerReservationDetailImpl>
  get copyWith => throw _privateConstructorUsedError;
}

SellerReservationSummary _$SellerReservationSummaryFromJson(
  Map<String, dynamic> json,
) {
  return _SellerReservationSummary.fromJson(json);
}

/// @nodoc
mixin _$SellerReservationSummary {
  int get reservationId => throw _privateConstructorUsedError;
  String get conceptTitle => throw _privateConstructorUsedError;
  int get price => throw _privateConstructorUsedError;
  String get confirmedAt => throw _privateConstructorUsedError;
  String get fulfillmentType => throw _privateConstructorUsedError;

  /// Serializes this SellerReservationSummary to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of SellerReservationSummary
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $SellerReservationSummaryCopyWith<SellerReservationSummary> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $SellerReservationSummaryCopyWith<$Res> {
  factory $SellerReservationSummaryCopyWith(
    SellerReservationSummary value,
    $Res Function(SellerReservationSummary) then,
  ) = _$SellerReservationSummaryCopyWithImpl<$Res, SellerReservationSummary>;
  @useResult
  $Res call({
    int reservationId,
    String conceptTitle,
    int price,
    String confirmedAt,
    String fulfillmentType,
  });
}

/// @nodoc
class _$SellerReservationSummaryCopyWithImpl<
  $Res,
  $Val extends SellerReservationSummary
>
    implements $SellerReservationSummaryCopyWith<$Res> {
  _$SellerReservationSummaryCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of SellerReservationSummary
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? reservationId = null,
    Object? conceptTitle = null,
    Object? price = null,
    Object? confirmedAt = null,
    Object? fulfillmentType = null,
  }) {
    return _then(
      _value.copyWith(
            reservationId: null == reservationId
                ? _value.reservationId
                : reservationId // ignore: cast_nullable_to_non_nullable
                      as int,
            conceptTitle: null == conceptTitle
                ? _value.conceptTitle
                : conceptTitle // ignore: cast_nullable_to_non_nullable
                      as String,
            price: null == price
                ? _value.price
                : price // ignore: cast_nullable_to_non_nullable
                      as int,
            confirmedAt: null == confirmedAt
                ? _value.confirmedAt
                : confirmedAt // ignore: cast_nullable_to_non_nullable
                      as String,
            fulfillmentType: null == fulfillmentType
                ? _value.fulfillmentType
                : fulfillmentType // ignore: cast_nullable_to_non_nullable
                      as String,
          )
          as $Val,
    );
  }
}

/// @nodoc
abstract class _$$SellerReservationSummaryImplCopyWith<$Res>
    implements $SellerReservationSummaryCopyWith<$Res> {
  factory _$$SellerReservationSummaryImplCopyWith(
    _$SellerReservationSummaryImpl value,
    $Res Function(_$SellerReservationSummaryImpl) then,
  ) = __$$SellerReservationSummaryImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({
    int reservationId,
    String conceptTitle,
    int price,
    String confirmedAt,
    String fulfillmentType,
  });
}

/// @nodoc
class __$$SellerReservationSummaryImplCopyWithImpl<$Res>
    extends
        _$SellerReservationSummaryCopyWithImpl<
          $Res,
          _$SellerReservationSummaryImpl
        >
    implements _$$SellerReservationSummaryImplCopyWith<$Res> {
  __$$SellerReservationSummaryImplCopyWithImpl(
    _$SellerReservationSummaryImpl _value,
    $Res Function(_$SellerReservationSummaryImpl) _then,
  ) : super(_value, _then);

  /// Create a copy of SellerReservationSummary
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? reservationId = null,
    Object? conceptTitle = null,
    Object? price = null,
    Object? confirmedAt = null,
    Object? fulfillmentType = null,
  }) {
    return _then(
      _$SellerReservationSummaryImpl(
        reservationId: null == reservationId
            ? _value.reservationId
            : reservationId // ignore: cast_nullable_to_non_nullable
                  as int,
        conceptTitle: null == conceptTitle
            ? _value.conceptTitle
            : conceptTitle // ignore: cast_nullable_to_non_nullable
                  as String,
        price: null == price
            ? _value.price
            : price // ignore: cast_nullable_to_non_nullable
                  as int,
        confirmedAt: null == confirmedAt
            ? _value.confirmedAt
            : confirmedAt // ignore: cast_nullable_to_non_nullable
                  as String,
        fulfillmentType: null == fulfillmentType
            ? _value.fulfillmentType
            : fulfillmentType // ignore: cast_nullable_to_non_nullable
                  as String,
      ),
    );
  }
}

/// @nodoc
@JsonSerializable()
class _$SellerReservationSummaryImpl implements _SellerReservationSummary {
  const _$SellerReservationSummaryImpl({
    required this.reservationId,
    required this.conceptTitle,
    required this.price,
    required this.confirmedAt,
    required this.fulfillmentType,
  });

  factory _$SellerReservationSummaryImpl.fromJson(Map<String, dynamic> json) =>
      _$$SellerReservationSummaryImplFromJson(json);

  @override
  final int reservationId;
  @override
  final String conceptTitle;
  @override
  final int price;
  @override
  final String confirmedAt;
  @override
  final String fulfillmentType;

  @override
  String toString() {
    return 'SellerReservationSummary(reservationId: $reservationId, conceptTitle: $conceptTitle, price: $price, confirmedAt: $confirmedAt, fulfillmentType: $fulfillmentType)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$SellerReservationSummaryImpl &&
            (identical(other.reservationId, reservationId) ||
                other.reservationId == reservationId) &&
            (identical(other.conceptTitle, conceptTitle) ||
                other.conceptTitle == conceptTitle) &&
            (identical(other.price, price) || other.price == price) &&
            (identical(other.confirmedAt, confirmedAt) ||
                other.confirmedAt == confirmedAt) &&
            (identical(other.fulfillmentType, fulfillmentType) ||
                other.fulfillmentType == fulfillmentType));
  }

  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  int get hashCode => Object.hash(
    runtimeType,
    reservationId,
    conceptTitle,
    price,
    confirmedAt,
    fulfillmentType,
  );

  /// Create a copy of SellerReservationSummary
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$SellerReservationSummaryImplCopyWith<_$SellerReservationSummaryImpl>
  get copyWith =>
      __$$SellerReservationSummaryImplCopyWithImpl<
        _$SellerReservationSummaryImpl
      >(this, _$identity);

  @override
  Map<String, dynamic> toJson() {
    return _$$SellerReservationSummaryImplToJson(this);
  }
}

abstract class _SellerReservationSummary implements SellerReservationSummary {
  const factory _SellerReservationSummary({
    required final int reservationId,
    required final String conceptTitle,
    required final int price,
    required final String confirmedAt,
    required final String fulfillmentType,
  }) = _$SellerReservationSummaryImpl;

  factory _SellerReservationSummary.fromJson(Map<String, dynamic> json) =
      _$SellerReservationSummaryImpl.fromJson;

  @override
  int get reservationId;
  @override
  String get conceptTitle;
  @override
  int get price;
  @override
  String get confirmedAt;
  @override
  String get fulfillmentType;

  /// Create a copy of SellerReservationSummary
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$SellerReservationSummaryImplCopyWith<_$SellerReservationSummaryImpl>
  get copyWith => throw _privateConstructorUsedError;
}

/// @nodoc
mixin _$SellerProposalForm {
  int? get requestId => throw _privateConstructorUsedError;
  int? get proposalId => throw _privateConstructorUsedError;
  String get conceptTitle => throw _privateConstructorUsedError;
  String get mainFlowers => throw _privateConstructorUsedError;
  String get subFlowers => throw _privateConstructorUsedError;
  String get concept => throw _privateConstructorUsedError;
  String get wrapping => throw _privateConstructorUsedError;
  String get recommendation => throw _privateConstructorUsedError;
  int get price => throw _privateConstructorUsedError;
  String? get selectedSlotKind => throw _privateConstructorUsedError;
  String? get selectedSlotValue => throw _privateConstructorUsedError;
  String? get expiresAt => throw _privateConstructorUsedError;

  /// Create a copy of SellerProposalForm
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $SellerProposalFormCopyWith<SellerProposalForm> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $SellerProposalFormCopyWith<$Res> {
  factory $SellerProposalFormCopyWith(
    SellerProposalForm value,
    $Res Function(SellerProposalForm) then,
  ) = _$SellerProposalFormCopyWithImpl<$Res, SellerProposalForm>;
  @useResult
  $Res call({
    int? requestId,
    int? proposalId,
    String conceptTitle,
    String mainFlowers,
    String subFlowers,
    String concept,
    String wrapping,
    String recommendation,
    int price,
    String? selectedSlotKind,
    String? selectedSlotValue,
    String? expiresAt,
  });
}

/// @nodoc
class _$SellerProposalFormCopyWithImpl<$Res, $Val extends SellerProposalForm>
    implements $SellerProposalFormCopyWith<$Res> {
  _$SellerProposalFormCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of SellerProposalForm
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? requestId = freezed,
    Object? proposalId = freezed,
    Object? conceptTitle = null,
    Object? mainFlowers = null,
    Object? subFlowers = null,
    Object? concept = null,
    Object? wrapping = null,
    Object? recommendation = null,
    Object? price = null,
    Object? selectedSlotKind = freezed,
    Object? selectedSlotValue = freezed,
    Object? expiresAt = freezed,
  }) {
    return _then(
      _value.copyWith(
            requestId: freezed == requestId
                ? _value.requestId
                : requestId // ignore: cast_nullable_to_non_nullable
                      as int?,
            proposalId: freezed == proposalId
                ? _value.proposalId
                : proposalId // ignore: cast_nullable_to_non_nullable
                      as int?,
            conceptTitle: null == conceptTitle
                ? _value.conceptTitle
                : conceptTitle // ignore: cast_nullable_to_non_nullable
                      as String,
            mainFlowers: null == mainFlowers
                ? _value.mainFlowers
                : mainFlowers // ignore: cast_nullable_to_non_nullable
                      as String,
            subFlowers: null == subFlowers
                ? _value.subFlowers
                : subFlowers // ignore: cast_nullable_to_non_nullable
                      as String,
            concept: null == concept
                ? _value.concept
                : concept // ignore: cast_nullable_to_non_nullable
                      as String,
            wrapping: null == wrapping
                ? _value.wrapping
                : wrapping // ignore: cast_nullable_to_non_nullable
                      as String,
            recommendation: null == recommendation
                ? _value.recommendation
                : recommendation // ignore: cast_nullable_to_non_nullable
                      as String,
            price: null == price
                ? _value.price
                : price // ignore: cast_nullable_to_non_nullable
                      as int,
            selectedSlotKind: freezed == selectedSlotKind
                ? _value.selectedSlotKind
                : selectedSlotKind // ignore: cast_nullable_to_non_nullable
                      as String?,
            selectedSlotValue: freezed == selectedSlotValue
                ? _value.selectedSlotValue
                : selectedSlotValue // ignore: cast_nullable_to_non_nullable
                      as String?,
            expiresAt: freezed == expiresAt
                ? _value.expiresAt
                : expiresAt // ignore: cast_nullable_to_non_nullable
                      as String?,
          )
          as $Val,
    );
  }
}

/// @nodoc
abstract class _$$SellerProposalFormImplCopyWith<$Res>
    implements $SellerProposalFormCopyWith<$Res> {
  factory _$$SellerProposalFormImplCopyWith(
    _$SellerProposalFormImpl value,
    $Res Function(_$SellerProposalFormImpl) then,
  ) = __$$SellerProposalFormImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({
    int? requestId,
    int? proposalId,
    String conceptTitle,
    String mainFlowers,
    String subFlowers,
    String concept,
    String wrapping,
    String recommendation,
    int price,
    String? selectedSlotKind,
    String? selectedSlotValue,
    String? expiresAt,
  });
}

/// @nodoc
class __$$SellerProposalFormImplCopyWithImpl<$Res>
    extends _$SellerProposalFormCopyWithImpl<$Res, _$SellerProposalFormImpl>
    implements _$$SellerProposalFormImplCopyWith<$Res> {
  __$$SellerProposalFormImplCopyWithImpl(
    _$SellerProposalFormImpl _value,
    $Res Function(_$SellerProposalFormImpl) _then,
  ) : super(_value, _then);

  /// Create a copy of SellerProposalForm
  /// with the given fields replaced by the non-null parameter values.
  @pragma('vm:prefer-inline')
  @override
  $Res call({
    Object? requestId = freezed,
    Object? proposalId = freezed,
    Object? conceptTitle = null,
    Object? mainFlowers = null,
    Object? subFlowers = null,
    Object? concept = null,
    Object? wrapping = null,
    Object? recommendation = null,
    Object? price = null,
    Object? selectedSlotKind = freezed,
    Object? selectedSlotValue = freezed,
    Object? expiresAt = freezed,
  }) {
    return _then(
      _$SellerProposalFormImpl(
        requestId: freezed == requestId
            ? _value.requestId
            : requestId // ignore: cast_nullable_to_non_nullable
                  as int?,
        proposalId: freezed == proposalId
            ? _value.proposalId
            : proposalId // ignore: cast_nullable_to_non_nullable
                  as int?,
        conceptTitle: null == conceptTitle
            ? _value.conceptTitle
            : conceptTitle // ignore: cast_nullable_to_non_nullable
                  as String,
        mainFlowers: null == mainFlowers
            ? _value.mainFlowers
            : mainFlowers // ignore: cast_nullable_to_non_nullable
                  as String,
        subFlowers: null == subFlowers
            ? _value.subFlowers
            : subFlowers // ignore: cast_nullable_to_non_nullable
                  as String,
        concept: null == concept
            ? _value.concept
            : concept // ignore: cast_nullable_to_non_nullable
                  as String,
        wrapping: null == wrapping
            ? _value.wrapping
            : wrapping // ignore: cast_nullable_to_non_nullable
                  as String,
        recommendation: null == recommendation
            ? _value.recommendation
            : recommendation // ignore: cast_nullable_to_non_nullable
                  as String,
        price: null == price
            ? _value.price
            : price // ignore: cast_nullable_to_non_nullable
                  as int,
        selectedSlotKind: freezed == selectedSlotKind
            ? _value.selectedSlotKind
            : selectedSlotKind // ignore: cast_nullable_to_non_nullable
                  as String?,
        selectedSlotValue: freezed == selectedSlotValue
            ? _value.selectedSlotValue
            : selectedSlotValue // ignore: cast_nullable_to_non_nullable
                  as String?,
        expiresAt: freezed == expiresAt
            ? _value.expiresAt
            : expiresAt // ignore: cast_nullable_to_non_nullable
                  as String?,
      ),
    );
  }
}

/// @nodoc

class _$SellerProposalFormImpl extends _SellerProposalForm {
  const _$SellerProposalFormImpl({
    this.requestId,
    this.proposalId,
    this.conceptTitle = '',
    this.mainFlowers = '',
    this.subFlowers = '',
    this.concept = '',
    this.wrapping = '',
    this.recommendation = '',
    this.price = 0,
    this.selectedSlotKind,
    this.selectedSlotValue,
    this.expiresAt,
  }) : super._();

  @override
  final int? requestId;
  @override
  final int? proposalId;
  @override
  @JsonKey()
  final String conceptTitle;
  @override
  @JsonKey()
  final String mainFlowers;
  @override
  @JsonKey()
  final String subFlowers;
  @override
  @JsonKey()
  final String concept;
  @override
  @JsonKey()
  final String wrapping;
  @override
  @JsonKey()
  final String recommendation;
  @override
  @JsonKey()
  final int price;
  @override
  final String? selectedSlotKind;
  @override
  final String? selectedSlotValue;
  @override
  final String? expiresAt;

  @override
  String toString() {
    return 'SellerProposalForm(requestId: $requestId, proposalId: $proposalId, conceptTitle: $conceptTitle, mainFlowers: $mainFlowers, subFlowers: $subFlowers, concept: $concept, wrapping: $wrapping, recommendation: $recommendation, price: $price, selectedSlotKind: $selectedSlotKind, selectedSlotValue: $selectedSlotValue, expiresAt: $expiresAt)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$SellerProposalFormImpl &&
            (identical(other.requestId, requestId) ||
                other.requestId == requestId) &&
            (identical(other.proposalId, proposalId) ||
                other.proposalId == proposalId) &&
            (identical(other.conceptTitle, conceptTitle) ||
                other.conceptTitle == conceptTitle) &&
            (identical(other.mainFlowers, mainFlowers) ||
                other.mainFlowers == mainFlowers) &&
            (identical(other.subFlowers, subFlowers) ||
                other.subFlowers == subFlowers) &&
            (identical(other.concept, concept) || other.concept == concept) &&
            (identical(other.wrapping, wrapping) ||
                other.wrapping == wrapping) &&
            (identical(other.recommendation, recommendation) ||
                other.recommendation == recommendation) &&
            (identical(other.price, price) || other.price == price) &&
            (identical(other.selectedSlotKind, selectedSlotKind) ||
                other.selectedSlotKind == selectedSlotKind) &&
            (identical(other.selectedSlotValue, selectedSlotValue) ||
                other.selectedSlotValue == selectedSlotValue) &&
            (identical(other.expiresAt, expiresAt) ||
                other.expiresAt == expiresAt));
  }

  @override
  int get hashCode => Object.hash(
    runtimeType,
    requestId,
    proposalId,
    conceptTitle,
    mainFlowers,
    subFlowers,
    concept,
    wrapping,
    recommendation,
    price,
    selectedSlotKind,
    selectedSlotValue,
    expiresAt,
  );

  /// Create a copy of SellerProposalForm
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$SellerProposalFormImplCopyWith<_$SellerProposalFormImpl> get copyWith =>
      __$$SellerProposalFormImplCopyWithImpl<_$SellerProposalFormImpl>(
        this,
        _$identity,
      );
}

abstract class _SellerProposalForm extends SellerProposalForm {
  const factory _SellerProposalForm({
    final int? requestId,
    final int? proposalId,
    final String conceptTitle,
    final String mainFlowers,
    final String subFlowers,
    final String concept,
    final String wrapping,
    final String recommendation,
    final int price,
    final String? selectedSlotKind,
    final String? selectedSlotValue,
    final String? expiresAt,
  }) = _$SellerProposalFormImpl;
  const _SellerProposalForm._() : super._();

  @override
  int? get requestId;
  @override
  int? get proposalId;
  @override
  String get conceptTitle;
  @override
  String get mainFlowers;
  @override
  String get subFlowers;
  @override
  String get concept;
  @override
  String get wrapping;
  @override
  String get recommendation;
  @override
  int get price;
  @override
  String? get selectedSlotKind;
  @override
  String? get selectedSlotValue;
  @override
  String? get expiresAt;

  /// Create a copy of SellerProposalForm
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$SellerProposalFormImplCopyWith<_$SellerProposalFormImpl> get copyWith =>
      throw _privateConstructorUsedError;
}
