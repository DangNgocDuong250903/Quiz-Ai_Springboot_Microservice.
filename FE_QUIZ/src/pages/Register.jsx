import { useForm } from "react-hook-form";
import { Link, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { BsShare } from "react-icons/bs";
import { ImConnection } from "react-icons/im";
import { AiOutlineInteraction } from "react-icons/ai";
import { IoMdEye, IoMdEyeOff } from "react-icons/io";
import { useMutationHook } from "~/hooks/useMutationHook";
import * as AccessService from "~/services/AccessService";
import { FaCircleExclamation } from "react-icons/fa6";
import { notification } from "antd";
import Button from "~/components/Button";
import TextInput from "~/components/TextInput";

const RegisterPage = () => {
  const [hide, setHide] = useState("hide");
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
  } = useForm({ mode: "onChange" });

  const mutation = useMutationHook((data) => AccessService.register(data));
  const { data: dataRegister, isPending, isSuccess, isError } = mutation;

  useEffect(() => {
    if (isSuccess) {
      navigate("/login", {
        state: {
          dataUser: dataRegister?.data?.result,
        },
      });
    } else if (isError) {
      notification.error({
        placement: "bottomRight",
        message: "User is existed!",
      });
    }
  }, [isSuccess, isError]);

  const onSubmit = async (data) => {
    const { dateOfBirth } = data;
    const [year, month, day] = dateOfBirth.split("-");
    const formattedDate = `${day}/${month}/${year}`;
    const newData = { ...data, dateOfBirth: formattedDate };
    mutation.mutate(newData);
  };

  return (
    <div>
      <div className="bg-[#FAFAFA]  w-full h-[100vh] flex items-center justify-center p-6">
        <div className="w-full md:w-2/3 h-fit lg:h-full 2xl:h-5/6 py-8 lg:py-0 flex flex-row-reverse bg-primary rounded-xl overflow-hidden shadow-2xl border-1 border-[#D5D5D5]">
          {/* phai */}

          <div className="w-full lg:w-1/2 h-full p-10 2xl:px-20 flex flex-col justify-center shadow-xl">
            {/* header */}
            <div className="w-full flex flex-col gap-y-2">
              <div className="w-full flex items-center gap-x-2">
                <span className="text-2xl text-ascent-1 font-semibold">
                  QuizMaster
                </span>
              </div>
              <p className="text-blue text-base font-semibold">Tạo tài khoản</p>
            </div>

            <form
              className="flex flex-col pb-2"
              onSubmit={handleSubmit(onSubmit)}
            >
              <div className="w-full flex flex-col gap-2">
                <div className="w-full flex flex-col  lg:flex-row gap-1 md:gap-2">
                  <TextInput
                    name="firstName"
                    label="Họ"
                    placeholder="Họ"
                    type="text"
                    styles={`w-full h-10 ${
                      errors.firstName ? "border-red-600" : ""
                    }`}
                    iconRight={
                      errors.firstName ? (
                        <FaCircleExclamation color="red" />
                      ) : (
                        ""
                      )
                    }
                    toolTip={errors.firstName ? errors.firstName.message : ""}
                    register={register("firstName", {
                      required: "Họ là bắt buộc",
                      minLength: {
                        value: 1,
                        message: "Họ phải có ít nhất 1 ký tự",
                      },
                      maxLength: {
                        value: 50,
                        message: "Họ không được vượt quá 50 ký tự",
                      },
                    })}
                  />

                  <TextInput
                    name="lastName"
                    label="Tên"
                    placeholder="Tên"
                    type="text"
                    styles={`w-full h-10 ${
                      errors.lastName ? "border-red-600" : ""
                    }`}
                    iconRight={
                      errors.lastName ? <FaCircleExclamation color="red" /> : ""
                    }
                    toolTip={errors.lastName ? errors.lastName?.message : ""}
                    register={register("lastName", {
                      required: "Tên là bắt buộc",
                      minLength: {
                        value: 1,
                        message: "Tên phải có ít nhất 1 ký tự",
                      },
                      maxLength: {
                        value: 50,
                        message: "Tên không được vượt quá 50 ký tự",
                      },
                    })}
                  />
                </div>

                <div className="w-full flex flex-col  lg:flex-row gap-1 md:gap-2">
                  <TextInput
                    name="username"
                    label="Tên người dùng"
                    placeholder="Tên người dùng"
                    type="text"
                    styles={`w-full h-10 ${
                      errors.username ? "border-red-600" : ""
                    }`}
                    iconRight={
                      errors.username ? <FaCircleExclamation color="red" /> : ""
                    }
                    toolTip={errors.username ? errors.username.message : ""}
                    register={register("username", {
                      required: "Tên người dùng là bắt buộc",
                      minLength: {
                        value: 1,
                        message: "Tên người dùng phải có ít nhất 1 ký tự",
                      },
                      maxLength: {
                        value: 50,
                        message: "Tên người dùng không được vượt quá 50 ký tự",
                      },
                      validate: {
                        noSpaces: (value) =>
                          !/\s/.test(value) ||
                          "Tên người dùng không được chứa khoảng trắng",
                      },
                    })}
                  />

                  <TextInput
                    name="dateOfBirth"
                    type="date"
                    label="Ngày sinh"
                    styles={`w-full h-10 ${
                      errors.dateOfBirth ? "border-red-600" : ""
                    }`}
                    register={register("dateOfBirth", {
                      required: "Ngày sinh là bắt buộc",
                    })}
                    toolTipInput={
                      errors.dateOfBirth ? errors.dateOfBirth?.message : ""
                    }
                  />
                </div>

                {/* gender */}
                <div className="w-full flex flex-col mt-2">
                  <p className="text-ascent-2 text-sm mb-2">Giới tính</p>

                  <div className="w-full h-10 flex flex-col lg:flex-row gap-1 md:gap-2">
                    <div
                      className={`flex w-full items-center justify-between bg-secondary rounded border border-[#66666690] outline-none text-sm text-ascent-1 px-4 py-2.5 ${
                        errors.gender ? "border-red-600" : ""
                      }`}
                    >
                      <label className="text-ascent-1" htmlFor="male">
                        Nam
                      </label>
                      <input
                        type="radio"
                        id="male"
                        value="male"
                        {...register("gender", {
                          required: "Giới tính là bắt buộc",
                        })}
                      />
                    </div>

                    <div
                      className={`flex w-full items-center justify-between bg-secondary rounded border border-[#66666690] outline-none text-sm text-ascent-1 px-4 py-2.5 ${
                        errors.gender ? "border-red-600" : ""
                      }`}
                    >
                      <label className="text-ascent-1" htmlFor="female">
                        Nữ
                      </label>
                      <input
                        type="radio"
                        id="female"
                        value="female"
                        {...register("gender", {
                          required: "Gender is required",
                        })}
                      />
                    </div>

                    <div
                      className={`flex w-full items-center justify-between bg-secondary rounded border border-[#66666690] outline-none text-sm text-ascent-1 px-4 py-2.5 ${
                        errors.gender ? "border-red-600" : ""
                      }`}
                    >
                      <label className="text-ascent-1" htmlFor="other">
                        Khác
                      </label>
                      <input
                        type="radio"
                        id="other"
                        value="other"
                        {...register("gender", {
                          required: "Gender is required",
                        })}
                      />
                    </div>
                  </div>
                </div>

                <div className="w-full flex flex-col lg:flex-row gap-1 md:gap-2">
                  <TextInput
                    name="email"
                    placeholder="email@ex.com"
                    label="Địa chỉ email"
                    type="email"
                    register={register("email", {
                      required: "Địa chỉ email là bắt buộc",
                      validate: {
                        noSpaces: (value) =>
                          !/\s/.test(value) ||
                          "Email không được chứa khoảng trắng",
                      },
                    })}
                    styles={`w-full h-10 ${
                      errors.email ? "border-red-600" : ""
                    }`}
                    iconRight={
                      errors.email ? <FaCircleExclamation color="red" /> : ""
                    }
                    toolTip={errors.email ? errors.email?.message : ""}
                  />
                  <TextInput
                    name="phoneNumber"
                    label="Số điện thoại"
                    placeholder="Số điện thoại"
                    type="text"
                    styles={`w-full h-10 ${
                      errors.phoneNumber ? "border-red-600" : ""
                    }`}
                    iconRight={
                      errors.phoneNumber ? (
                        <FaCircleExclamation color="red" />
                      ) : (
                        ""
                      )
                    }
                    toolTip={
                      errors.phoneNumber ? errors.phoneNumber.message : ""
                    }
                    register={register("phoneNumber", {
                      required: "Số điện thoại là bắt buộc",
                      pattern: {
                        value: /^[0-9]{10,11}$/,
                        message: "Số điện thoại không hợp lệ",
                      },
                    })}
                  />
                </div>

                <div className="w-full flex flex-col lg:flex-row gap-1 md:gap-2">
                  <TextInput
                    name="mssv"
                    label="Mã số sinh viên"
                    placeholder="Xác nhận mật khẩu"
                    type="number"
                    styles={`w-full h-10 ${
                      errors.mssv ? "border-red-600" : ""
                    }`}
                    iconRight={
                      errors.mssv ? <FaCircleExclamation color="red" /> : ""
                    }
                    toolTip={errors.mssv ? errors.mssv?.message : ""}
                    register={register("mssv", {
                      required: "Vui lòng nhập MSSV",
                    })}
                  />

                  <TextInput
                    name="password"
                    label="Mật khẩu"
                    placeholder="Mật khẩu"
                    type={hide === "hide" ? "password" : "text"}
                    iconRight={
                      errors.password ? (
                        <FaCircleExclamation color="red" />
                      ) : hide === "hide" ? (
                        <IoMdEyeOff
                          className="cursor-pointer"
                          onClick={() => setHide("show")}
                        />
                      ) : (
                        <IoMdEye
                          className="cursor-pointer"
                          onClick={() => setHide("hide")}
                        />
                      )
                    }
                    iconRightStyles="right-4"
                    styles={`w-full h-10 ${
                      errors.password ? "border-red-600" : ""
                    }`}
                    toolTip={errors.password ? errors.password?.message : ""}
                    register={register("password", {
                      required: "Mật khẩu là bắt buộc",
                      minLength: {
                        value: 8,
                        message: "Mật khẩu phải có ít nhất 8 ký tự",
                      },
                      maxLength: {
                        value: 64,
                        message: "Mật khẩu không được vượt quá 64 ký tự",
                      },
                      pattern: {
                        value:
                          /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,64}$/,
                        message:
                          "Mật khẩu phải chứa ít nhất 1 chữ hoa, 1 chữ thường, 1 số và 1 ký tự đặc biệt.",
                      },
                      validate: {
                        noSpaces: (value) =>
                          !/\s/.test(value) ||
                          "Mật khẩu không được chứa khoảng trắng",
                      },
                    })}
                  />
                </div>
              </div>

              <div className="relative">
                <Button
                  disable={isPending}
                  isLoading={isPending}
                  type="submit"
                  className="w-full mt-3 inline-flex justify-center rounded-md bg-[#065AD8] px-8 py-3 text-sm font-medium text-white outline-non hover:opacity-90"
                  title="Đăng ký"
                />
              </div>
            </form>

            <p className="text-ascent-2 text-sm text-center">
              Đã có tài khoản ?
              <Link
                to="/login"
                className="text-blue ml-2 cursor-pointer hover:opacity-90"
              >
                Đăng nhập
              </Link>
            </p>
          </div>
          <div className="hidden w-1/2 h-full lg:flex flex-col items-center justify-center bg-[#065AD8] shadow-xl">
            <div className="relative w-full flex items-center justify-center">
              <img
                src="/bg_register.jfif"
                className="w-48 2xl:w-64 h-48 2xl:h-64 rounded-full object-cover shadow-xl"
              />

              <div className="absolute flex items-center gap-1 bg-white right-10 top-10 py-2 px-5 rounded-full shadow-xl">
                <BsShare size={14} className="text-ascent-1" />
                <span className="text-ascent-1 text-xs font-medium">
                  Chia sẻ
                </span>
              </div>

              <div className="absolute flex items-center gap-1 bg-white left-10 top-6 py-2 px-5 rounded-full shadow-xl">
                <ImConnection className="text-ascent-1" />
                <span className="text-ascent-1 text-xs font-medium">
                  Kết nối
                </span>
              </div>

              <div className="absolute flex items-center gap-1 bg-white left-12 bottom-6 py-2 px-5 rounded-full shadow-xl">
                <AiOutlineInteraction className="text-ascent-1" />
                <span className="text-ascent-1 text-xs font-medium">
                  Tương tác
                </span>
              </div>
            </div>

            <div className="mt-16 text-center">
              <p className="text-white text-base">
                Kết nối với bạn bè và chia sẻ những điều thú vị
              </p>
              <span className="text-sm text-white/80">
                Kết nối với bạn bè và chia sẻ những điều thú vị
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;
