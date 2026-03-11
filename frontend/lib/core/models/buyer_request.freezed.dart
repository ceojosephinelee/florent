// coverage:ignore-file
// GENERATED CODE - DO NOT MODIFY BY HAND
// ignore_for_file: type=lint
// ignore_for_file: unused_element, deprecated_member_use, deprecated_member_use_from_same_package, use_function_type_syntax_for_parameters, unnecessary_const, avoid_init_to_null, invalid_override_different_default_values_named, prefer_expression_function_bodies, annotate_overrides, invalid_annotation_target, unnecessary_question_mark

part of 'buyer_request.dart';

// **************************************************************************
// FreezedGenerator
// **************************************************************************

T _$identity<T>(T value) => value;

final _privateConstructorUsedError = UnsupportedError(
  'It seems like you constructed your class using `MyClass._()`. This constructor is only meant to be used by freezed and you are not supposed to need it nor use it.\nPlease check the documentation here for more information: https://github.com/rrousselGit/freezed#adding-getters-and-methods-to-our-models',
);

BuyerRequestSummary _$BuyerRequestSummaryFromJson(Map<String, dynamic> json) {
  return _BuyerRequestSummary.fromJson(json);
}

/// @nodoc
mixin _$BuyerRequestSummary {
  int get requestId => throw _privateConstructorUsedError;
  String get status => throw _privateConstructorUsedError;
  String get budgetTier => throw _privateConstructorUsedError;
  String get fulfillmentType => throw _privateConstructorUsedError;
  String get fulfillmentDate => throw _privateConstructorUsedError;
  String get expiresAt => throw _privateConstructorUsedError;
  int get draftProposalCount => throw _privateConstructorUsedError;
  int get submittedProposalCount => throw _privateConstructorUsedError;
  List<String> get purposeTags => throw _privateConstructorUsedError;

  /// Serializes this BuyerRequestSummary to a JSON map.
  Map<String, dynamic> toJson() => throw _privateConstructorUsedError;

  /// Create a copy of BuyerRequestSummary
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  $BuyerRequestSummaryCopyWith<BuyerRequestSummary> get copyWith =>
      throw _privateConstructorUsedError;
}

/// @nodoc
abstract class $BuyerRequestSummaryCopyWith<$Res> {
  factory $BuyerRequestSummaryCopyWith(
    BuyerRequestSummary value,
    $Res Function(BuyerRequestSummary) then,
  ) = _$BuyerRequestSummaryCopyWithImpl<$Res, BuyerRequestSummary>;
  @useResult
  $Res call({
    int requestId,
    String status,
    String budgetTier,
    String fulfillmentType,
    String fulfillmentDate,
    String expiresAt,
    int draftProposalCount,
    int submittedProposalCount,
    List<String> purposeTags,
  });
}

/// @nodoc
class _$BuyerRequestSummaryCopyWithImpl<$Res, $Val extends BuyerRequestSummary>
    implements $BuyerRequestSummaryCopyWith<$Res> {
  _$BuyerRequestSummaryCopyWithImpl(this._value, this._then);

  // ignore: unused_field
  final $Val _value;
  // ignore: unused_field
  final $Res Function($Val) _then;

  /// Create a copy of BuyerRequestSummary
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
    Object? draftProposalCount = null,
    Object? submittedProposalCount = null,
    Object? purposeTags = null,
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
            draftProposalCount: null == draftProposalCount
                ? _value.draftProposalCount
                : draftProposalCount // ignore: cast_nullable_to_non_nullable
                      as int,
            submittedProposalCount: null == submittedProposalCount
                ? _value.submittedProposalCount
                : submittedProposalCount // ignore: cast_nullable_to_non_nullable
                      as int,
            purposeTags: null == purposeTags
                ? _value.purposeTags
                : purposeTags // ignore: cast_nullable_to_non_nullable
                      as List<String>,
          )
          as $Val,
    );
  }
}

/// @nodoc
abstract class _$$BuyerRequestSummaryImplCopyWith<$Res>
    implements $BuyerRequestSummaryCopyWith<$Res> {
  factory _$$BuyerRequestSummaryImplCopyWith(
    _$BuyerRequestSummaryImpl value,
    $Res Function(_$BuyerRequestSummaryImpl) then,
  ) = __$$BuyerRequestSummaryImplCopyWithImpl<$Res>;
  @override
  @useResult
  $Res call({
    int requestId,
    String status,
    String budgetTier,
    String fulfillmentType,
    String fulfillmentDate,
    String expiresAt,
    int draftProposalCount,
    int submittedProposalCount,
    List<String> purposeTags,
  });
}

/// @nodoc
class __$$BuyerRequestSummaryImplCopyWithImpl<$Res>
    extends _$BuyerRequestSummaryCopyWithImpl<$Res, _$BuyerRequestSummaryImpl>
    implements _$$BuyerRequestSummaryImplCopyWith<$Res> {
  __$$BuyerRequestSummaryImplCopyWithImpl(
    _$BuyerRequestSummaryImpl _value,
    $Res Function(_$BuyerRequestSummaryImpl) _then,
  ) : super(_value, _then);

  /// Create a copy of BuyerRequestSummary
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
    Object? draftProposalCount = null,
    Object? submittedProposalCount = null,
    Object? purposeTags = null,
  }) {
    return _then(
      _$BuyerRequestSummaryImpl(
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
        draftProposalCount: null == draftProposalCount
            ? _value.draftProposalCount
            : draftProposalCount // ignore: cast_nullable_to_non_nullable
                  as int,
        submittedProposalCount: null == submittedProposalCount
            ? _value.submittedProposalCount
            : submittedProposalCount // ignore: cast_nullable_to_non_nullable
                  as int,
        purposeTags: null == purposeTags
            ? _value._purposeTags
            : purposeTags // ignore: cast_nullable_to_non_nullable
                  as List<String>,
      ),
    );
  }
}

/// @nodoc
@JsonSerializable()
class _$BuyerRequestSummaryImpl implements _BuyerRequestSummary {
  const _$BuyerRequestSummaryImpl({
    required this.requestId,
    required this.status,
    required this.budgetTier,
    required this.fulfillmentType,
    required this.fulfillmentDate,
    required this.expiresAt,
    required this.draftProposalCount,
    required this.submittedProposalCount,
    final List<String> purposeTags = const [],
  }) : _purposeTags = purposeTags;

  factory _$BuyerRequestSummaryImpl.fromJson(Map<String, dynamic> json) =>
      _$$BuyerRequestSummaryImplFromJson(json);

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
  final int draftProposalCount;
  @override
  final int submittedProposalCount;
  final List<String> _purposeTags;
  @override
  @JsonKey()
  List<String> get purposeTags {
    if (_purposeTags is EqualUnmodifiableListView) return _purposeTags;
    // ignore: implicit_dynamic_type
    return EqualUnmodifiableListView(_purposeTags);
  }

  @override
  String toString() {
    return 'BuyerRequestSummary(requestId: $requestId, status: $status, budgetTier: $budgetTier, fulfillmentType: $fulfillmentType, fulfillmentDate: $fulfillmentDate, expiresAt: $expiresAt, draftProposalCount: $draftProposalCount, submittedProposalCount: $submittedProposalCount, purposeTags: $purposeTags)';
  }

  @override
  bool operator ==(Object other) {
    return identical(this, other) ||
        (other.runtimeType == runtimeType &&
            other is _$BuyerRequestSummaryImpl &&
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
            (identical(other.draftProposalCount, draftProposalCount) ||
                other.draftProposalCount == draftProposalCount) &&
            (identical(other.submittedProposalCount, submittedProposalCount) ||
                other.submittedProposalCount == submittedProposalCount) &&
            const DeepCollectionEquality().equals(
              other._purposeTags,
              _purposeTags,
            ));
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
    draftProposalCount,
    submittedProposalCount,
    const DeepCollectionEquality().hash(_purposeTags),
  );

  /// Create a copy of BuyerRequestSummary
  /// with the given fields replaced by the non-null parameter values.
  @JsonKey(includeFromJson: false, includeToJson: false)
  @override
  @pragma('vm:prefer-inline')
  _$$BuyerRequestSummaryImplCopyWith<_$BuyerRequestSummaryImpl> get copyWith =>
      __$$BuyerRequestSummaryImplCopyWithImpl<_$BuyerRequestSummaryImpl>(
        this,
        _$identity,
      );

  @override
  Map<String, dynamic> toJson() {
    return _$$BuyerRequestSummaryImplToJson(this);
  }
}

abstract class _BuyerRequestSummary implements BuyerRequestSummary {
  const factory _BuyerRequestSummary({
    required final int requestId,
    required final String status,
    required final String budgetTier,
    required final String fulfillmentType,
    required final String fulfillmentDate,
    required final String expiresAt,
    required final int draftProposalCount,
    required final int submittedProposalCount,
    final List<String> purposeTags,
  }) = _$BuyerRequestSummaryImpl;

  factory _BuyerRequestSummary.fromJson(Map<String, dynamic> json) =
      _$BuyerRequestSummaryImpl.fromJson;

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
  int get draftProposalCount;
  @override
  int get submittedProposalCount;
  @override
  List<String> get purposeTags;

  /// Create a copy of BuyerRequestSummary
  /// with the given fields replaced by the non-null parameter values.
  @override
  @JsonKey(includeFromJson: false, includeToJson: false)
  _$$BuyerRequestSummaryImplCopyWith<_$BuyerRequestSummaryImpl> get copyWith =>
      throw _privateConstructorUsedError;
}
