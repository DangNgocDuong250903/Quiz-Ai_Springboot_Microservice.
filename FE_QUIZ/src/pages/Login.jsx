import { useForm } from "react-hook-form";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { BsShare } from "react-icons/bs";
import { ImConnection } from "react-icons/im";
import { AiOutlineInteraction } from "react-icons/ai";
import { IoMdEye, IoMdEyeOff } from "react-icons/io";
import { useMutationHook } from "~/hooks/useMutationHook";
import * as AccessService from "~/services/AccessService";
import { FaCircleExclamation } from "react-icons/fa6";
import { jwtDecode } from "jwt-decode";
import { updateUser } from "~/redux/Slices/userSlice";
import { useDispatch } from "react-redux";
import { FcGoogle } from "react-icons/fc";
import { useGoogleLogin } from "@react-oauth/google";
import Button from "~/components/Button";
import TextInput from "~/components/TextInput";
import OtpVerification from "~/components/OtpVerification";

const LoginPage = () => {
  const navigate = useNavigate();
  const [hide, setHide] = useState("hide");
  const { state } = useLocation();
  const dispatch = useDispatch();
  const [userData, setUserData] = useState(null);
  const [open, setOpen] = useState(false);
  const handleClose = () => setOpen(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    mode: "onChange",
    defaultValues: {
      username: state?.dataUser?.username || "",
    },
  });

  const mutation = useMutationHook((newData) => AccessService.login(newData));
  const { data: dataLogin, isPending, isSuccess, isError } = mutation;

  const handleGetDetailUser = async ({ id, token }) => {
    const res = await AccessService.getDetailUserByUserId({ id });
    dispatch(updateUser({ ...res?.result, token }));
  };

  useEffect(() => {
    const handleLoginSuccess = async () => {
      if (dataLogin?.code === 1000 && dataLogin?.result?.token) {
        const token = dataLogin?.result?.token;
        localStorage.setItem("token", token);

        const decoded = jwtDecode(token);
        if (decoded?.userId) {
          await handleGetDetailUser({ id: decoded.userId, token });
        }

        navigate("/admin");
      } else if (dataLogin?.code === 1030) {
        setOpen(true);
      }
    };

    if (isSuccess) {
      handleLoginSuccess(); // chạy async function bên trong useEffect
    }
  }, [isSuccess, isError]);

  const onSubmit = async (data) => {
    const newData = { ...data, otp: 0 };
    setUserData(newData);
    mutation.mutate(newData);
  };

  const handleSuccessLoginGoogle = async (credential) => {
    const res = await AccessService.loginGoogle({
      access_token: credential?.access_token,
    });
    const token = res?.access_token;
    if (token) {
      localStorage.setItem("token", token);
      navigate("/");
      const decoded = jwtDecode(token);
      if (decoded?.userId) {
        handleGetDetailUser({ id: decoded?.userId, token });
      }
    }
  };

  const handleLoginGoogle = useGoogleLogin({
    flow: "implicit",
    onSuccess: handleSuccessLoginGoogle,
  });

  return (
    <div>
      <OtpVerification
        open={open}
        handleClose={handleClose}
        userData={userData}
      />
      <div className="bg-[#FAFAFA] w-full h-[100vh] flex items-center justify-center p-6 ">
        <div className="w-full md:w-2/3 h-fit lg:h-full 2xl:h-5/6 py-8 lg:py-0 flex bg-[#fff] rounded-xl overflow-hidden shadow-newFeed border-[#D5D5D5] border-solid border-x-[0.8px] border-y-[0.8px]">
          <div className="w-full lg:w-1/2 h-full p-10 2xl:px-20 flex flex-col justify-center ">
            <div className="w-full flex gap-2 items-center mb-3">
              <span className="text-2xl text-ascent-1 font-semibold">
                QuizMaster
              </span>
            </div>

            <p className="text-ascent-1 text-base font-semibold">
              Đăng nhập vào tài khoản của bạn
            </p>
            <span className="text-sm mt-2 text-ascent-2">
              Chào mừng bạn quay trở lại!
            </span>

            <form
              className="py-4 flex flex-col gap-5="
              onSubmit={handleSubmit(onSubmit)}
            >
              <div className="w-full flex flex-col gap-2">
                <TextInput
                  name="username"
                  placeholder="User Name"
                  label="User Name"
                  type="username"
                  register={register("username", {
                    required: "Tên người dùng là bắt buộc",
                    validate: {
                      noSpaces: (value) =>
                        !/\s/.test(value) ||
                        "User name must not contain spaces.",
                    },
                  })}
                  styles={`w-full rounded-xl ${
                    errors.username ? "border-red-600" : ""
                  }`}
                  iconRight={
                    errors.username ? <FaCircleExclamation color="red" /> : ""
                  }
                  iconRightStyles="right-5"
                  toolTip={errors.username ? errors.username?.message : ""}
                  labelStyles="ml-2"
                />

                <TextInput
                  name="password"
                  label="Password"
                  placeholder="Mật khẩu"
                  type={hide === "hide" ? "password" : "text"}
                  styles={`w-full rounded-xl  ${
                    errors.password ? "border-red-600" : ""
                  }`}
                  labelStyles="ml-2"
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
                  toolTip={errors.password ? errors.password?.message : ""}
                  iconRightStyles="right-5"
                  register={register("password", {
                    required: "Mật khẩu là bắt buộc",
                    validate: {
                      noSpaces: (value) =>
                        !/\s/.test(value) ||
                        "Mật khẩu không được chứa khoảng trắng",
                    },
                  })}
                />
              </div>

              <div className=" w-full mt-5 flex flex-col gap-y-2">
                <div className="relative">
                  <Button
                    disable={isPending}
                    isLoading={isPending}
                    type="submit"
                    title="Đăng nhập"
                    className="w-full inline-flex py-3 justify-center rounded-md bg-[#065AD8] border-1 border-borderNewFeed px-8 text-sm font-medium text-white outline-none hover:opacity-90"
                  />
                </div>
                <p className="text-ascent-2 text-sm text-center">
                  Không có tài khoản ?
                  <Link
                    to="/register"
                    className="text-blue font-medium ml-2 cursor-pointer hover:opacity-80"
                  >
                    Tạo tài khoản
                  </Link>
                </p>
              </div>
            </form>
          </div>
          <div className="hidden w-1/2 h-full lg:flex flex-col items-center justify-center bg-[#065AD8]">
            <div className="relative w-full flex items-center justify-center">
              <img
                src="/bg_login.jfif"
                alt="bg"
                className="w-48 2xl:w-64 h-48 2xl:h-64 rounded-full object-cover shadow-xl"
              />

              <div className="absolute flex items-center gap-1 bg-white  right-10 top-10 py-2 px-5 rounded-full shadow-xl">
                <BsShare size={14} className="text-ascent-1" />
                <span className="text-xs font-medium text-ascent-1">
                  Chia sẻ
                </span>
              </div>

              <div className="absolute flex items-center gap-1 bg-white left-10 top-6 py-2 px-5 rounded-full shadow-xl">
                <ImConnection size={14} className="text-ascent-1" />
                <span className="text-xs font-medium text-ascent-1">
                  Kết nối
                </span>
              </div>

              <div className="absolute flex items-center gap-1 bg-white left-12 bottom-6 py-2 px-5 rounded-full shadow-xl">
                <AiOutlineInteraction size={14} className="text-ascent-1" />
                <span className="text-xs font-medium text-ascent-1">
                  Tương tác
                </span>
              </div>
            </div>

            <div className="mt-16 text-center">
              <p className="text-white text-base">
                Tham gia cùng bạn bè và khám phá điều thú vị
              </p>
              <span className="text-sm  text-white/80">
                Chia sẻ điểm số và thành tích với bạn bè, cộng đồng
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
